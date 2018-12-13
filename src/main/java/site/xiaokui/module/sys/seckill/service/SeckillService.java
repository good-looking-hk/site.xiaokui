package site.xiaokui.module.sys.seckill.service;

import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.beetl.sql.core.OnConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import site.xiaokui.config.spring.RedisDao;
import site.xiaokui.module.base.service.BaseService;
import site.xiaokui.module.sys.seckill.dto.Execution;
import site.xiaokui.module.sys.seckill.dto.Exposer;
import site.xiaokui.module.sys.seckill.entity.SeckillProduct;
import site.xiaokui.module.sys.seckill.entity.SeckillStatusEnum;
import site.xiaokui.module.sys.seckill.entity.SuccessSeckilled;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;

/**
 * @author HK
 * @date 2018-10-03 19:35
 */
@Slf4j
@Service
public class SeckillService extends BaseService<SeckillProduct> {

    @Autowired
    private RedisDao redisDao;

    private static final String SALT = "&abc123~@xyz789$";

    public void resetDate() {
        redisDao.remove("1");
        redisDao.remove("2");
        redisDao.remove("3");
        SeckillProduct product = new SeckillProduct();
        Date date = new Date();
        product.setId(1);
        product.setNumber(10);
        product.setStartTime(DateUtil.beginOfDay(date));
        product.setEndTime(DateUtil.endOfDay(date));
        this.updateByIdIgnoreNull(product);

        product.setId(2);
        product.setNumber(20);
        product.setStartTime(DateUtil.offsetDay(date, 1));
        product.setEndTime(DateUtil.offsetDay(date, 2));
        this.updateByIdIgnoreNull(product);

        product.setId(3);
        product.setNumber(100);
        product.setStartTime(DateUtil.offsetDay(date, -2));
        product.setEndTime(DateUtil.offsetDay(date, -1));
        this.updateByIdIgnoreNull(product);
    }

    public Exposer exportSecKillUrl(Integer id) {
        // 缓存秒杀商品数据
        SeckillProduct product = redisDao.get(id, SeckillProduct.class);
        if (product == null) {
            product = getById(id);
            if (product == null) {
                return new Exposer(false, id);
            }
            redisDao.put(product, product.getId());
        }

        Date startTime = product.getStartTime();
        Date endTime = product.getEndTime();
        Date nowTime = new Date();
        // 若是秒杀未开启
        if (nowTime.getTime() < startTime.getTime() || nowTime.getTime() > endTime.getTime()) {
            return new Exposer(false, id, nowTime.getTime(), startTime.getTime(), endTime.getTime());
        }
        // 秒杀开启，返回秒杀商品的id、用给接口加密的md5
        String md5 = getMD5(id);
        return new Exposer(true, md5, id);
    }

    /**
     * @param id 秒杀商品id
     * @param phone 用户手机号码
     * @return 秒杀结果
     */
    public Execution executeSeckillProcedure(Integer id, String md5, String phone) {
        if (StringUtils.isEmpty(md5) || !md5.equals(getMD5(id))) {
            throw new RuntimeException("md5验证失败");
        }
        int result = sqlManager.executeOnConnection(new OnConnection<Integer>() {
            @Override
            public Integer call(Connection conn) throws SQLException {
                CallableStatement cs = conn.prepareCall("call execute_seckill(?, ?, ?, ?)");
                cs.setObject(1, id);
                cs.setObject(2, phone);
                cs.setObject(3, new Date());
                cs.registerOutParameter(4, Types.INTEGER);
                cs.execute();
                return cs.getInt(4);
            }
        });
        log.debug("执行秒杀，结果为" + result);
        if (SeckillStatusEnum.SUCCESS.getCode() == result) {
            SuccessSeckilled successSeckilled = sqlManager.single(SuccessSeckilled.class, id);
            return new Execution(id, SeckillStatusEnum.SUCCESS, successSeckilled);
        } else if (SeckillStatusEnum.END.getCode() == result) {
            return new Execution(id, SeckillStatusEnum.END);
        } else if (SeckillStatusEnum.REPEAT_KILL.getCode() == result) {
            return new Execution(id, SeckillStatusEnum.REPEAT_KILL);
        } else if (SeckillStatusEnum.INNER_ERROR.getCode() == result){
            return new Execution(id, SeckillStatusEnum.INNER_ERROR);
        }
        return null;
    }

    private String getMD5(Integer id) {
        String base = id + SALT;
        return DigestUtils.md5DigestAsHex(base.getBytes());
    }
}
