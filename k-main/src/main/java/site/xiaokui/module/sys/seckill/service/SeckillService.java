package site.xiaokui.module.sys.seckill.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.crypto.SecureUtil;
import lombok.extern.slf4j.Slf4j;
import org.beetl.sql.core.BeetlSQLException;
import org.beetl.sql.core.OnConnection;
import org.beetl.sql.core.SQLReady;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import site.xiaokui.base.service.BaseService;
import site.xiaokui.base.service.RedisService;
import site.xiaokui.module.sys.seckill.dto.Exposer;
import site.xiaokui.module.sys.seckill.dto.SeckillResult;
import site.xiaokui.module.sys.seckill.entity.SeckillProduct;
import site.xiaokui.module.sys.seckill.entity.SeckillStatusEnum;
import site.xiaokui.module.sys.seckill.entity.SuccessSeckilled;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;

/**
 * 秒杀系统核心逻辑
 *
 * @author HK
 * @date 2018-10-03 19:35
 */
@Slf4j
@Service
public class SeckillService extends BaseService<SeckillProduct> {

//    @Autowired
    private RedisService redisService;

    private static final String SALT = "&abc123~@xyz789$";

    /**
     * 重置模拟数据，3组数据分别为正在抢购，明天抢购，抢购已结束
     */
    public void resetDate() {
//        redisService.remove("1");
//        redisService.remove("2");
//        redisService.remove("3");
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

        // 保留第一条用于测试重复秒杀
        this.executeDelSql("delete from success_seckilled where id != 1");
    }

    public Exposer exportSecKillUrl(Integer pid) {
        // 缓存秒杀商品数据
//        SeckillProduct product = redisService.set(String.valueOf(pid), SeckillProduct.class, 60);
//        if (product == null) {
//            product = getById(pid);
//            if (product == null) {
//                return new Exposer(false, pid);
//            }
//            redisService.get(product.getId(), product);
//        }
//
//        Date startTime = product.getStartTime();
//        Date endTime = product.getEndTime();
//        Date nowTime = new Date();
//        // 若是秒杀未开始或已结束
//        if (nowTime.getTime() < startTime.getTime() || nowTime.getTime() > endTime.getTime()) {
//            return new Exposer(false, pid, nowTime.getTime(), startTime.getTime(), endTime.getTime());
//        }
//        // 秒杀开启，返回秒杀商品的id、用给接口加密的md5
//        String md5 = getMD5(pid, "11");
//        return new Exposer(true, md5, pid);
        return null;
    }

    /**
     * @param pid   秒杀商品id
     * @param phone 用户手机号码
     * @return 秒杀结果
     */
    public SeckillResult executeSeckillProcedure(Integer pid, String md5, String phone) {
        if (StringUtils.isEmpty(md5) || !md5.equals(getMD5(pid, phone))) {
            throw new RuntimeException("md5验证失败");
        }
        int result = sqlManager.executeOnConnection(new OnConnection<Integer>() {
            @Override
            public Integer call(Connection conn) throws SQLException {
                CallableStatement cs = conn.prepareCall("call execute_seckill(?, ?, ?, ?)");
                cs.setObject(1, pid);
                cs.setObject(2, phone);
                cs.setObject(3, new Date());
                cs.registerOutParameter(4, Types.INTEGER);
                cs.execute();
                return cs.getInt(4);
            }
        });
        if (SeckillStatusEnum.SUCCESS.getCode() == result) {
            return new SeckillResult(pid, SeckillStatusEnum.SUCCESS);
        } else if (SeckillStatusEnum.END.getCode() == result) {
            return new SeckillResult(pid, SeckillStatusEnum.END);
        } else if (SeckillStatusEnum.REPEAT_KILLED.getCode() == result) {
            return new SeckillResult(pid, SeckillStatusEnum.REPEAT_KILLED);
        }
        return new SeckillResult(pid, SeckillStatusEnum.INNER_ERROR);
    }

    /**
     * 这里的异常不可捕获，不然事务管理器会感知不到
     * @param pid 商品id
     * @param md5 加密md5
     * @param phone 手机号
     * @return 秒杀结果
     * @throws BeetlSQLException 当秒杀明细插入失败时抛出
     * @throws RuntimeException 当减库存失败时抛出
     */
    @Transactional(rollbackFor = RuntimeException.class)
    public SeckillResult executeSeckillTransaction(Integer pid, String md5, String phone) throws RuntimeException {
        if (StringUtils.isEmpty(md5) || !md5.equals(getMD5(pid, phone))) {
            throw new RuntimeException("md5验证失败");
        }
        // 执行秒杀逻辑：减库存 + 插入秒杀明细
        Date date = new Date();
        SuccessSeckilled seckilled = new SuccessSeckilled();
        seckilled.setSeckillTime(date);
        seckilled.setPhone(phone);
        seckilled.setSeckillProductId(pid);
        int insertCount;
        try {
            insertCount = this.sqlManager.insert(SuccessSeckilled.class, seckilled);
        } catch (BeetlSQLException e) {
            throw new RuntimeException("重复秒杀");
        }
        if (insertCount <= 0) {
            // 一般来说不会走到这，在这之前会抛出异常
            log.info("插入记录失败");
            return new SeckillResult(pid, SeckillStatusEnum.REPEAT_KILLED);
        } else {
            int updateCount = this.sqlManager.executeUpdate(
                    new SQLReady("update seckill_product set number = number - 1 where id = ? and number >= 1" +
                            " and end_time > ? and start_time < ?", pid, date, date));
            if (updateCount <= 0) {
                throw new RuntimeException("秒杀结束");
            }
            return new SeckillResult(pid, SeckillStatusEnum.SUCCESS);
        }
    }

    public String getMD5(Integer pid, String phone) {
        String data = pid + SALT + phone;
        return SecureUtil.md5(data);
    }
}
