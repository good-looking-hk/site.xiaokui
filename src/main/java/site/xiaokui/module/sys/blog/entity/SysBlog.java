package site.xiaokui.module.sys.blog.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import site.xiaokui.module.base.entity.ParentEntity;

import java.util.Comparator;

/**
 * @author HK
 * @date 2018-06-24 21:24
 * 注意，这里的{@code name}为html文件名称，一般来说，不允许改变
 * title为博客标题，最初情况下，title与name同值，用户可以改变title但是不可以改变name
 * </p>
 * 另外就是createTime是博客最初写的时间，而modifiedTime是博客上传的时间，其中createTime是允许主动改变的，
 * 而modifiedTime是被动改变的
 * 对于博客的排序，参见 {@link BlogComparator}
 */
@ToString(callSuper = true)
@Getter@Setter
public class SysBlog extends ParentEntity {

    /**
     * 博客标题
     */
    private String title;

    /**
     * 所属文件夹
     */
    private String dir;

    /**
     * 是否对外显示
     */
    private Integer status;

    /**
     * 拥有者id
     */
    private Integer userId;

    /**
     * 不参与序列化的字段，存储博客html文件的内部地址
     */
    private transient String filePath;

    /**
     * 对外公开的博客地址，blogPath依赖于filePath
     */
    private transient String blogPath;

    private transient String preBlog;

    private transient String preBlogTitle;

    private transient String nextBlog;

    private transient String nextBlogTitle;

    /// 留作纪念

    /**
     * SysBlog的排序需要调用此比较器进行比较
     */
    public static class BlogComparator implements Comparator<SysBlog> {
        @Override
        public int compare(SysBlog o1, SysBlog o) {
            if (!o1.dir.equals(o.getDir())) {
                return o1.dir.compareTo(o.getDir());
            }
            if (!o1.orderNum.equals(o.getOrderNum())) {
                return o1.orderNum.compareTo(o.getOrderNum());
            }
            // 后面上传的排在前面
            return o1.id.compareTo(o.getId()) * -1;
        }
    }
}
