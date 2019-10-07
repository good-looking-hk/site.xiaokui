package site.xiaokui.module.sys.seckill.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import site.xiaokui.module.base.entity.BaseEntity;

import java.util.Date;

/**
 * @author HK
 * @date 2018-10-03 17:10
 */
@ToString(callSuper = true)
@Getter
@Setter
public class SeckillProduct extends BaseEntity {

    /**
     * 库存数量
     */
    private Integer number;

    /**
     * 秒杀开始时间
     */
    private Date startTime;

    /**
     * 秒杀结束时间
     */
    private Date endTime;

    /**
     * 描述信息
     */
    private String description;
}
