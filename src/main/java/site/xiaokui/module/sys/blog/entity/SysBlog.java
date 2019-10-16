package site.xiaokui.module.sys.blog.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import site.xiaokui.common.util.StringUtil;
import site.xiaokui.module.base.entity.ParentEntity;

import java.util.Comparator;

/**
 * @author HK
 * @date 2018-06-24 21:24
 * 注意，这里的{@code name}为html文件名称，一般来说，不允许改变
 * title为博客标题，最初情况下，title与name同值，用户可以改变title但是不可以改变name
 * 另外就是createTime是博客最初写的时间，而modifiedTime是博客上传的时间，其中createTime是允许主动改变的，
 * 而modifiedTime是被动改变的
 * 对于博客的排序，参见 {@link DirComparator}
 * 对于博客字数的统计算法，参见 {@link UploadBlog#characterCount}
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
     * 总访问量
     */
    private Integer viewCount;

    /**
     * 昨天访问量
     */
    private Integer yesterday;

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

    /**
     * 今天访问量
     */
    private transient Integer today;

    /**
     * 字符数量
     */
    private Integer characterCount;

    /// 留作纪念

    /**
     * 根据目录排序
     */
    public static class DirComparator implements Comparator<SysBlog> {
        @Override
        public int compare(SysBlog o1, SysBlog o2) {
            if (!o1.dir.equals(o2.getDir())) {
                return o1.dir.compareTo(o2.getDir());
            }
            // 博客可以不指定序号，而只依靠日期作为排序
            if (o1.orderNum != null && o2.orderNum != null) {
                if (!o1.orderNum.equals(o2.getOrderNum())) {
                    return o1.orderNum.compareTo(o2.getOrderNum());
                }
            }
            if (!o1.createTime.equals(o2.getCreateTime())) {
                return o1.createTime.compareTo(o2.getCreateTime());
            }
            // 后面上传的排在前面
            return o1.id.compareTo(o2.getId()) * -1;
        }
    }

    /**
     * 根据时间排序，默认从小到大
     */
    public static class DateComparator implements Comparator<SysBlog> {
        @Override
        public int compare(SysBlog o1, SysBlog o2) {
            int r = o2.createTime.compareTo(o1.createTime);
            if (r != 0) {
                return r;
            }
            return o2.id.compareTo(o1.id);
        }
    }
}
