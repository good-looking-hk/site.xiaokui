package site.xiaokui.oauth2.service;

import org.beetl.sql.core.query.Query;
import org.springframework.stereotype.Service;
import site.xiaokui.base.service.BaseService;
import site.xiaokui.base.util.StringUtil;
import site.xiaokui.oauth2.entity.ShiroUser;
import site.xiaokui.oauth2.entity.SysUser;

import java.util.List;

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

    public ShiroUser wrapShiroUser(SysUser user) {
        ShiroUser shiroUser = new ShiroUser();
        shiroUser.setUserId(user.getId());
        shiroUser.setUsername(user.getName());
        shiroUser.setEmail(user.getEmail());
        shiroUser.setPhone(user.getPhone());
        // 如果用户没有指定博客空间，默认为用户id
        if (StringUtil.isEmpty(user.getBlogSpace())) {
            shiroUser.setBlogSpace(String.valueOf(user.getId()));
        } else {
            shiroUser.setBlogSpace(user.getBlogSpace());
        }
        shiroUser.setAvatar(user.getAvatar());
        shiroUser.setSelfDescription(user.getSelfDescription());
        shiroUser.setLastLoginTime(user.getLastLoginTime());
        shiroUser.setLastLoginIp(user.getLastLoginIp());
        shiroUser.setCreateTime(user.getCreateTime());
        return shiroUser;
    }
}
