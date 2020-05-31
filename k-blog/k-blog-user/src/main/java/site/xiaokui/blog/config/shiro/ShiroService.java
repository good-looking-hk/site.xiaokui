package site.xiaokui.blog.config.shiro;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import site.xiaokui.base.util.StringUtil;
import site.xiaokui.blog.dao.MenuDao;
import site.xiaokui.blog.entity.SysUser;
import site.xiaokui.blog.service.ServiceFactory;
import site.xiaokui.blog.service.UserService;

import java.util.Date;
import java.util.List;

/**
 * 为Shiro的用户模块、角色模块和权限模块提供各种支持
 * @author HK
 * @date 2018-05-21 22:07
 */
@Service
public class ShiroService {

    @Autowired
    private UserService userService;

    @Autowired
    private MenuDao menuDao;

    public void updateLoginTimeAndIP(Long id, Date date, String ip) {
        SysUser user = new SysUser();
        user.setId(id);
        user.setLastLoginIp(ip);
        user.setLastLoginTime(date);
        userService.updateByIdIgnoreNull(user);
    }

    /**
     * 根据用户名或邮箱或手机获取登录用户
     * @param loginName 用户名或邮箱
     * @return 用户信息
     */
    public SysUser findUser(String loginName) {
        SysUser user = userService.getUserByName(loginName);
        if (user == null) {
            user = userService.getUserByEmail(loginName);
        }
        if (user == null) {
            user = userService.getUserByPhone(loginName);
        }
        return user;
    }

    /**
     * 获取权限列表通过角色id
     * @param roleId 角色id
     * @return 权限列表
     */
    public List<String> findPermissionsByRoleId(Integer roleId) {
        return menuDao.findPermissionsByRoleId(roleId);
    }


    /**
     * 根据系统用户获取波包装Shiro的用户，使之携带跟多的用户信息
     * @param user 系统用户
     * @return 封装的ShiroUser
     */
    public ShiroUser wrapUser(SysUser user) {
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
        shiroUser.setRoleId(user.getRoleId());
        shiroUser.setRoleName(ServiceFactory.me().getRoleName(user.getRoleId()));
        return shiroUser;
    }
}
