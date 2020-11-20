package seckill.entity;

import lombok.Getter;

/**
 * @author HK
 * @date 2018-10-03 16:43
 */
public enum SeckillStatusEnum {

    /**
     * 秒杀状态
     */
    END(0,"秒杀结束"), SUCCESS(1,"秒杀成功"), REPEAT_KILLED(-1,"重复秒杀,已存在秒杀记录"), INNER_ERROR(-2,"服务器内部异常");

    @Getter
    private int code;

    @Getter
    private String value;

    SeckillStatusEnum(int code, String value){
        this.code = code;
        this.value = value;
    }
}
