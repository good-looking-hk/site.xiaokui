package site.xiaokui.config.shiro;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 自定义Authentication对象，使得Subject除了携带用户的登录名外还可以携带更多信息
 *
 * @author HK
 * @date 2018-05-21 19:33
 */
@Data
public class ShiroUser implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer userId;

    private String username;

    private String email;

    private String phone;

    private String avatar;

    private String blogSpace;

    private String selfDescription;

    private Date lastLoginTime;

    private Integer roleId;

    private String roleName;

    private Date createTime;

    private String lastLoginIp;

    private String currentIp;
}
