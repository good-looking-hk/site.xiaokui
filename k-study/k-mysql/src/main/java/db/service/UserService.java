package db.service;

import db.dao.UserBalanceDao;
import db.dao.UserDao;
import db.entity.User;
import db.entity.UserBalance;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.List;

/**
 * TransactionDefinition.PROPAGATION_NEVER：以非事务方式运行，如果当前存在事务，则抛出异常 - 在事务环境运行就保存
 *
 * TransactionDefinition.PROPAGATION_NOT_SUPPORTED：以非事务方式运行，如果当前存在事务，则把当前事务挂起 - 如果在事务环境运行，就挂起事务，自己以非事务运行
 *
 * Spring默认传播机制
 * TransactionDefinition.PROPAGATION_REQUIRED：如果当前存在事务，则加入该事务；如果当前没有事务，则创建一个新的事务 - 如果存在事务则加入，否则新建
 *
 * TransactionDefinition.PROPAGATION_REQUIRES_NEW：创建一个新的事务，如果当前存在事务，则把当前事务挂起 - 新建一个事务，独立于外部事务
 *
 * TransactionDefinition.PROPAGATION_SUPPORTS：如果当前存在事务，则加入该事务；如果当前没有事务，则以非事务的方式继续运行 - 有事务环境则加入，没有也行
 *
 * TransactionDefinition.PROPAGATION_MANDATORY：如果当前存在事务，则加入该事务；如果当前没有事务，则抛出异常 - 只能加入事务环境，没有事务环境则抛错
 *
 * TransactionDefinition.PROPAGATION_NESTED：如果当前存在事务，则创建一个事务作为当前事务的嵌套事务来运行；如果当前没有事务，则该取值等价于TransactionDefinition.PROPAGATION_REQUIRED。
 *
 * {@link org.springframework.transaction.support.AbstractPlatformTransactionManager#getTransaction} 方法包含对事务传播机制的特殊处理
 * {@link org.springframework.transaction.interceptor.AbstractFallbackTransactionAttributeSource#computeTransactionAttribute} 方法决定了Spring只会代理public方法
 *
 * 自调用可能会出现事务失效，如以下几种情况所示
 * -1 调到了非事务代理方法，而事务非代理方法调用了原对象（非代理对象）的事务方法，导致事务切面没加进去
 * -2 调用事务方法1，然后在事务方法1内调用了事务方法2，这样就导致了出现前面的问题，事务方法2的切面没加进去
 * -3 解决办法：1自己注入自己 2暴露代理对象 @EnableAspectJAutoProxy(exposeProxy = true)，((UserService) AopContext.currentProxy()).initUser(user)
 * @author HK@
 * @date 2021-03-17 17:49
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserDao userDao;

    private final UserBalanceDao userBalanceDao;

    /**
     * Spring官方使用例子，显式声明事务
     * 无论是底层还是代码层面，依赖的实现都是依赖于接口 PlatformTransactionManager
     * - DataSourceTransactionManager： Spring jdbc或iBatis使用
     * - HibernateTransactionManager： Hibernate高版本使用
     * - JpaTransactionManager：JPA使用
     * - JtaTransactionManager：JTA管理跨库事务
     */
    private final TransactionTemplate transactionTemplate;

    public List<User> allUser() {
        return userDao.findAll();
    }

    public User findFirst() {
        return userDao.selectByPrimaryKey(1L);
    }

    public void insert(User user) {
        userDao.insertSelective(user);
    }

    public int update(User user) {
        return userDao.updateByPrimaryKeySelective(user);
    }

    public User querySingle(Long id) {
        return userDao.selectByPrimaryKey(id);
    }

    /**
     * 通过注解声明事务，事务可正常工作
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = {Exception.class})
    public void initUser(User user) {
        userDao.insertSelective(user);
        UserBalance userBalance = new UserBalance();
        userBalance.setUserId(user.getId());
        userBalance.setUserBalance(new BigDecimal("100"));
        userBalanceDao.insertSelective(userBalance);
        throw new RuntimeException();
    }

    /**
     * 代码显式调用事务，事务可正常工作
     */
    public boolean initUser1(User user) {
        // transactionTemplate=PROPAGATION_REQUIRED,ISOLATION_DEFAULT, transactionTemplateClass=class org.springframework.transaction.support.TransactionTemplate
        log.info("transactionTemplate={}, transactionTemplateClass={}", transactionTemplate, transactionTemplate.getClass());
        // 对于 void 类型可用 TransactionCallbackWithoutResult 替代
        return transactionTemplate.execute(new TransactionCallback<Boolean>() {
            @Override
            public Boolean doInTransaction(TransactionStatus status) {
                userDao.insertSelective(user);
                UserBalance userBalance = new UserBalance();
                userBalance.setUserId(user.getId());
                userBalance.setUserBalance(new BigDecimal("100"));
                userBalanceDao.insertSelective(userBalance);
//                 throw new RuntimeException();
                return true;
            }
        });
    }

    /**
     * 测试通过，事务不生效，这种情况一般出现的比较少
     */
    public void initUser2(User user) {
        initUser21(user);
    }

    @Transactional(rollbackFor = Exception.class)
    private void initUser21(User user) {
        userDao.insertSelective(user);
        UserBalance userBalance = new UserBalance();
        userBalance.setUserId(user.getId());
        userBalance.setUserBalance(new BigDecimal("100"));
        userBalanceDao.insertSelective(userBalance);
        throw new RuntimeException();
    }

    public void initUser3(User user) {
        initUser(user);
    }
}
