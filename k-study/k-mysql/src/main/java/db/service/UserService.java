package db.service;

import db.dao.UserBalanceDao;
import db.dao.UserDao;
import db.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author HK
 * @date 2021-03-17 17:49
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserDao userDao;

    private final UserBalanceDao userBalanceDao;

    public List<User> allUser() {
        System.out.println(userDao.getClass());
        return userDao.findAll();
    }

    public User findFirst() {
        return userDao.selectByPrimaryKey(1L);
    }

    public void insert(User user) {
        userDao.insertSelective(user);
    }

    @Transactional()
    public void initUser(User user) {
        userDao.insertSelective(user);
        userBalanceDao.toString();
    }
}
