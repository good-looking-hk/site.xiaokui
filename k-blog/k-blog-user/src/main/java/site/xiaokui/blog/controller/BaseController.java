package site.xiaokui.blog.controller;

import org.apache.shiro.subject.Subject;
import site.xiaokui.base.entity.enums.SexTypeEnum;
import site.xiaokui.base.entity.enums.UserStatusEnum;
import site.xiaokui.blog.config.shiro.ShiroKit;
import site.xiaokui.blog.config.shiro.ShiroUser;
import site.xiaokui.blog.entity.SysUser;
import site.xiaokui.blog.entity.enums.RoleTypeEnum;

import java.util.Date;

/**
 * @author HK
 * @date 2020-07-29 17:19
 */
public class BaseController extends site.xiaokui.base.controller.BaseController {

    protected final static ShiroKit SHIRO = ShiroKit.getInstance();

    protected Subject getSubject() {
        return SHIRO.getSubject();
    }

    protected ShiroUser getUser() {
        return SHIRO.getUser();
    }

    protected Long getUserId() {
        if (getUser() == null) {
            return null;
        }
        return SHIRO.getUser().getUserId();
    }

    protected Integer getRoleId() {
        if (getUser() == null) {
            return null;
        }
        return SHIRO.getUser().getRoleId();
    }

    /**
     * 为用户分配一些默认的属性，如十位随机密码盐，默认头像，默认个性前面，状态，性别，状态，默认的用户角色
     */
    protected SysUser initDefaultUser(String username, String email, String password) {
        SysUser user = new SysUser();
        user.setName(username);
        user.setEmail(email);
        user.setSalt(SHIRO.fastSalt());
        user.setPassword(SHIRO.md5(password, user.getSalt()));
        user.setCreateTime(new Date());
        user.setStatus(UserStatusEnum.OK.getCode());
        user.setSex(SexTypeEnum.UNKNOWN.getCode());
        user.setRoleId(RoleTypeEnum.USER.getCode());
        return user;
    }
}
