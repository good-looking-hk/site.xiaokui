package site.xiaokui.oauth2.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import site.xiaokui.base.entity.BaseEntity;

import java.util.Date;

/**
 * @author HK
 * @date 2018-05-20 20:31
 */
@ToString(callSuper = true)
@Getter@Setter
public class SysUser extends BaseEntity {

    private String password;

    private String email;

    private String phone;

    private String blogSpace;

    private String selfDescription;

    private String avatar;

    private String salt;

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