package site.xiaokui.module.sys.blog.entity;

import lombok.Data;
import site.xiaokui.module.sys.user.entity.SysUser;

import java.util.List;

/**
 * @author HK
 * @date 2018-06-27 18:21
 */
@Data
public class BlogUser {

    private String name;

    private String selfDescription;

    private String avatar;

    private String blogSpace;

    private SysBlog blog;

    private List<List<SysBlog>> blogList;

    public BlogUser(SysUser user) {
        this.name = user.getName();
        this.avatar = user.getAvatar();
        this.blogSpace = user.getBlogSpace();
        this.selfDescription = user.getSelfDescription();
    }

    public BlogUser(String name, String avatar, String blogSpace) {
        this.name = name;
        this.avatar = avatar;
        this.blogSpace = blogSpace;
    }
}
