package site.xiaokui.module.sys.user.service;

import org.springframework.stereotype.Service;
import site.xiaokui.config.shiro.ShiroKit;
import site.xiaokui.module.base.service.BaseService;
import site.xiaokui.module.sys.user.entity.SysUser;

/**
 * @author HK
 * @date 2018-05-20 21:36
 */
@Service
public class UserService extends BaseService<SysUser> {

    public SysUser getUserByName(String name) {
        SysUser user = new SysUser();
        user.setName(name);
        return matchOne(user);
    }

    public SysUser getUserByEmail(String email) {
        SysUser user = new SysUser();
        user.setEmail(email);
        return matchOne(user);
    }

    public SysUser getUserByPhone(String phone) {
        SysUser user = new SysUser();
        user.setPhone(phone);
        return matchOne(user);
    }

    public SysUser getUserByBlogSpace(String blogSpace) {
        SysUser user = new SysUser();
        user.setBlogSpace(blogSpace);
        return matchOne(user);
    }

    public boolean resetPwd(Integer id) {
        return resetPwd(id, "123456");

    }

    public boolean resetPwd(Integer id, String newPassword) {
        SysUser user = new SysUser();
        user.setId(id);
        user.setSalt(ShiroKit.getInstance().fastSalt());
        user.setPassword(ShiroKit.getInstance().md5(newPassword, user.getSalt()));
        return updateByIdIgnoreNull(user);
    }

    public boolean roleIdIsInUse(Integer roleId) {
        SysUser user = new SysUser();
        user.setRoleId(roleId);
        user = matchOne(user);
        return user != null;
    }
}
