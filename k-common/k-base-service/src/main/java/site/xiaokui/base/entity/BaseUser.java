package site.xiaokui.base.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

/**
 * 本项目中公用用户实体类，各子系统可以有不同的细节差异
 * @author HK
 * @date 2018-05-20 20:31
 */
@ToString(callSuper = true)
@Getter@Setter
public class BaseUser extends BaseEntity {

    private String password;

    private String email;

    private String phone;

    private String blogSpace;

    private String selfDescription;

    private String avatar;

    private String salt;

    /**
     * 性别(1：男  2：女  3：保密）
     */
    private Integer sex;

    private Integer roleId;

    /**
     * 状态(1：启用  2：冻结  3：删除）
     */
    private Integer status;

    private Date lastLoginTime;

    private String lastLoginIp;

    /**
     * 受保护页面密码
     */
    private String proPassword;
}