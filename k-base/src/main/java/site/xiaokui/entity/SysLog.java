package site.xiaokui.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 系统日志实体类
 * @see site.xiaokui.base.aop.annotation.Log
 * @author HK
 * @date 2019-10-07 12:42
 */
@ToString
public class SysLog extends BaseEntity {

    @Getter@Setter
    protected String content;

    @Getter@Setter
    protected String remark;
}
