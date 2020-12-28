package site.xiaokui.domain;

import cn.hutool.core.util.StrUtil;
import lombok.Data;
import me.zhengjie.modules.system.domain.User;
import me.zhengjie.modules.system.service.dto.UserDto;

import java.util.List;

/**
 * @author HK
 * @date 2018-06-27 18:21
 */
@Data
public class BlogUser {

    private Long id;

    private String name;

    private String selfDescription;

    private String avatar;

    private String blogSpace;

    private SysBlog blog;

    private int pri, pro, pub, pageTotal, dirCount;

    private List<List<SysBlog>> blogList;

    public BlogUser(UserDto user) {
        this.id = user.getId();
        this.name = user.getNickName();
        this.avatar = "/avatar/" + user.getAvatarName();
        // 判断用户是否自定义博客空间名称
        if (StrUtil.isEmpty(user.getBlogSpace())) {
            this.setBlogSpace(String.valueOf(user.getId()));
        } else {
            this.blogSpace = user.getBlogSpace();
        }
        this.selfDescription = user.getNickName();
    }

    public BlogUser(String name, String avatar, String blogSpace) {
        this.name = name;
        this.avatar = avatar;
        this.blogSpace = blogSpace;
    }
}
