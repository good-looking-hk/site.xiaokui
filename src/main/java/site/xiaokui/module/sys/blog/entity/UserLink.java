package site.xiaokui.module.sys.blog.entity;

import lombok.Getter;
import lombok.Setter;
import site.xiaokui.module.base.entity.BaseEntity;

/**
 * 用户自定义链接，用于在个性化展示
 *
 * @author HK
 * @date 2019-02-23 16:14
 */
@Getter@Setter
public class UserLink extends BaseEntity {

    private Integer userId;
    private String title;
    /**
     * 是否需要特殊标识
     */
    private Boolean dot;

    public UserLink() {
    }

    public UserLink(Integer id, Integer userId, String title, String name) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.title = title;
    }
}
