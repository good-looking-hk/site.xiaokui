package site.xiaokui.seckill.dto;

import lombok.Data;
import site.xiaokui.module.sys.seckill.entity.SeckillStatusEnum;
import site.xiaokui.module.sys.seckill.entity.SuccessSeckilled;

/**
 * 秒杀结果
 * @author HK
 * @date 2018-10-03 22:41
 */
@Data
public class SeckillResult {

    /**
     * 秒杀商品id
     */
    private Integer id;

    /**
     * 秒杀执行结果状态
     */
    private int status;

    /**
     * 秒杀执行结果状态描述
     */
    private String statusInfo;

    /**
     * 秒杀成功对象id
     */
    private SuccessSeckilled successSeckilled;

    public SeckillResult(Integer id, SeckillStatusEnum status, SuccessSeckilled successSeckilled) {
        this.id = id;
        this.status = status.getCode();
        this.statusInfo = status.getValue();
        this.successSeckilled = successSeckilled;
    }

    public SeckillResult(Integer id, SeckillStatusEnum status) {
        this.id = id;
        this.status = status.getCode();
        this.statusInfo = status.getValue();
    }
}
