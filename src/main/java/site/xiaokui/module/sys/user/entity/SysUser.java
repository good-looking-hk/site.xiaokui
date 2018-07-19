package site.xiaokui.module.sys.user.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import site.xiaokui.module.base.entity.BaseEntity;

import java.util.Date;

/**
 * @author HK
 * @date 2018-05-20 20:31
 */
@ToString(callSuper = true)
@Getter@Setter
public class SysUser extends BaseEntity<SysMenu> {

    private String password;

    private String email;

    private String blogSpace;

    private String selfDescription;

    private String avatar;

    private String salt;

    private Integer sex;

    private Integer roleId;

    private Integer deptId;

    /**
     * 状态(1：启用  2：冻结  3：删除）
     */
    private Integer status;

    private Date lastLoginTime;

    private String lastLoginIp;
}