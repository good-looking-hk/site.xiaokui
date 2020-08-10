package site.xiaokui.seckill.dto;

import lombok.Data;

/**
 * 秒杀暴露对象
 * @author HK
 * @date 2018-10-03 22:28
 */
@Data
public class Exposer {

    /**
     * 是否开启秒杀
     */
    private boolean exposed;

    /**
     * 加密措施
     */
    private String md5;

    /**
     * 秒杀商品id
     */
    private Integer id;

    /**
     * 系统当前时间戳
     */
    private long now;

    /**
     * 秒杀开启时间
     */
    private long start;

    /**
     * 秒杀结束时间
     */
    private long end;

    public Exposer(boolean exposed, String md5, Integer id) {
        this.exposed = exposed;
        this.md5 = md5;
        this.id = id;
    }

    public Exposer(boolean exposed, Integer id, long now, long start, long end) {
        this.exposed = exposed;
        this.id = id;
        this.now = now;
        this.start = start;
        this.end = end;
    }

    public Exposer(boolean exposed, Integer id) {
        this.exposed = exposed;
        this.id = id;
    }
}
