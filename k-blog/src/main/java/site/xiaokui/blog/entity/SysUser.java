package site.xiaokui.blog.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import site.xiaokui.base.entity.BaseUser;

/**
 * @author HK
 * @date 2018-06-27 18:21
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysUser extends BaseUser {
    private String blogSpace;
}
