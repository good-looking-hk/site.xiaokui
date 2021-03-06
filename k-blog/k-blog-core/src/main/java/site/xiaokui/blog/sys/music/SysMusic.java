package site.xiaokui.blog.sys.music;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import site.xiaokui.base.entity.BaseEntity;

/**
 * @author HK
 * @date 2019-06-08 20:53
 */
@Getter@Setter@ToString(callSuper = true)
public class SysMusic extends BaseEntity {

    private Integer userId;

    private String author;

    private Integer orderNum;

    /**
     * 留作扩展
     */
    private String moreInfo;
}
