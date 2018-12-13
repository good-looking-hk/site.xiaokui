package site.xiaokui.module.sys.seckill.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author HK
 * @date 2018-10-03 22:42
 */
@Data
public class SuccessSeckilled {

    private Integer id;
    /**
     * 秒杀产品id
     */
    private Integer seckillProductId;

    /**
     * 用户手机号码
     */
    private String phone;

    /**
     * 秒杀成功时间
     */
    private Date seckillTime;

    /**
     * 创建时间，timestamp类型，由数据库自动更新，代码层不需要干涉
     */
    private Date createTime;
}
