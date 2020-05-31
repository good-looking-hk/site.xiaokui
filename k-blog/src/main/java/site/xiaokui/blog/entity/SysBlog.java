package site.xiaokui.blog.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import site.xiaokui.entity.ParentEntity;

import java.util.Comparator;
import java.util.Date;

/**
 * @author HK
 * @date 2018-06-24 21:24
 * 注意，这里的{@code name}为html文件名称，一般来说，不允许改变
 * title为博客标题，最初情况下，title与name同值，用户可以改变title但是不可以改变name
 * 另外就是createTime是博客最初写的时间，而modifiedTime是博客上传的时间，其中createTime是允许主动改变的，
 * 而modifiedTime是被动改变的
 * 对于博客的排序，参见 {@link DirComparator}
 * 对于博客字数的统计算法，参见 {@link UploadBlog#determineCharCount}
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

    /**
     * 博客更新时间，手动更新
     */
    private Date updateTime;

    private transient Integer recommendValue;

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
        private boolean orderByCreateTime = true;
        public DateComparator(boolean orderByCreateTimeNotByUpdateTime) {
            this.orderByCreateTime = orderByCreateTimeNotByUpdateTime;
        }

        @Override
        public int compare(SysBlog o1, SysBlog o2) {
            if (orderByCreateTime) {
                int r = o2.createTime.compareTo(o1.createTime);
                if (r != 0) {
                    return r;
                }
                return o2.id.compareTo(o1.id);
            } else {
                Date updateTime1, updateTime2;
                if (o1.updateTime == null) {
                    updateTime1 = o1.createTime;
                } else {
                    updateTime1 = o1.updateTime;
                }
                if (o2.updateTime == null) {
                    updateTime2 = o2.createTime;
                } else {
                    updateTime2 = o2.updateTime;
                }

                return updateTime2.compareTo(updateTime1);
            }
        }
    }

    /**
     * 推荐值比较器，具体算法思路大致如下：创建时间影响占比0.1、总阅读量占比0.35、昨日阅读量占比0.15，更新时间占比0.4
     * 如果该项没有值则忽略，假设有如下数据
     *     序号 名称 创建时间 更新时间 昨日阅读量 总阅读量
     *     1   博客1 20200101 20200102 10    100
     *     2   博客2 20200202 20200220 200   200
     *     3   博客3 20200301 20200311 2     3
     */
    public static class RecommendComparator implements Comparator<SysBlog> {
        @Override
        public int compare(SysBlog o1, SysBlog o2) {
            if (o1.getRecommendValue() == null) {
                o1.recommendValue = o1.calculateRecommendValue();
            }
            if (o2.getRecommendValue() == null) {
                o2.recommendValue = o2.calculateRecommendValue();
            }
            return o2.recommendValue - o1.recommendValue;
        }
    }

    /**
     * 有待后续改进
     */
    public int calculateRecommendValue() {
        Date now = new Date();
        double createTimeValue = 0, updateTimeValue = 7, yesterdayValue = 2, viewCountValue = 1, characterValue = 0;
        if (this.getCreateTime() != null) {
            // 这个值越大，则推荐值越低，负相关
            createTimeValue = (double) (now.getTime() - this.getCreateTime().getTime()) / (24 * 60 * 60 * 1000);
        }
        if (this.getUpdateTime() != null) {
            // 这个值越小，则推荐值越高，负相关
            updateTimeValue = (double) (now.getTime() - this.getUpdateTime().getTime()) / (24 * 60 * 60 * 1000);
        }
        if (this.getYesterday() != null) {
            // 正相关
            yesterdayValue = (this.getYesterday() + 2) * 2.1 + 10;
            if (yesterdayValue > 200) {
                yesterdayValue = (yesterdayValue - 150) / 4.7 + 20;
            }
        }
        if (this.getViewCount() != null) {
            // 正相关
            viewCountValue = (this.getViewCount() + 1) * 1.7 + 20;
            if (yesterdayValue > 300) {
                yesterdayValue = (yesterdayValue - 200) / 6.3 + 30;
            }
        }
        if (createTimeValue > 365) {
            createTimeValue /= 4.8;
        } else {
            createTimeValue = createTimeValue / 4.7 + 25;
        }
        if (updateTimeValue > 30) {
            updateTimeValue = updateTimeValue / 11 + 1;
        } else {
            updateTimeValue = updateTimeValue / 2.8 - 9;
        }
        if (this.getCharacterCount() != null) {
            characterValue = (double) this.characterCount / 650 + 2;
        }
        double value = 1150 - createTimeValue  - (updateTimeValue * 49) + yesterdayValue + viewCountValue + characterValue;
        if (this.name.contains("**") || this.title.contains("**")) {
            value += 500;
        } else if (this.name.contains("*") || this.title.contains("*")) {
            value += 200;
        }
        this.recommendValue = (int) value;
        return this.recommendValue;
    }
}
