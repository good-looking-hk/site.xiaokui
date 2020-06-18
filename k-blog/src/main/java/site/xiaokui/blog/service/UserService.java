package site.xiaokui.blog.service;

import org.beetl.sql.core.query.Query;
import org.springframework.stereotype.Service;
import site.xiaokui.base.service.BaseService;
import site.xiaokui.blog.config.shiro.ShiroKit;
import site.xiaokui.blog.entity.SysUser;

import java.util.Date;
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

    public boolean resetPwd(Long id) {
        return resetPwd(id, "123456");
    }

    public List<SysUser> allBlogUser() {
        Query<SysUser> query = this.createQuery();
        query.andNotEq("blog_space", "").andIsNotNull("blog_space");
        return this.query(query);
    }

    public boolean resetPwd(Long id, String newPassword) {
        SysUser user = new SysUser();
        user.setId(id);
        user.setSalt(ShiroKit.getInstance().fastSalt());
        user.setPassword(ShiroKit.getInstance().md5(newPassword, user.getSalt()));
        return updateByIdIgnoreNull(user);
    }
}
