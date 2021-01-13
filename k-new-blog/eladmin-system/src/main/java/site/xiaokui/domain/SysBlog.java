/*
 *  Copyright 2019-2020 Zheng Jie
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package site.xiaokui.domain;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import me.zhengjie.utils.DateUtil;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;

/**
 * @author hk
 * @website https://el-admin.vip
 * @description /
 * @date 2020-12-01
 **/
@Entity
@Data
@Table(name = "sys_blog")
public class SysBlog implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @ApiModelProperty(value = "博客id")
    private Long id;

    @Column(name = "dir")
    @ApiModelProperty(value = "博客目录")
    private String dir;

    @Column(name = "title")
    @ApiModelProperty(value = "博客标题")
    private String title;

    @Column(name = "file_name")
    @ApiModelProperty(value = "博客文件名称")
    private String fileName;

    @Column(name = "user_id")
    @ApiModelProperty(value = "拥有者id")
    private Long userId;

    @Column(name = "blog_type")
    @ApiModelProperty(value = "博客类型")
    private String blogType;

    @Column(name = "create_date")
    @ApiModelProperty(value = "创建时间")
    private Integer createDate;

    @Column(name = "last_upload_time")
    @ApiModelProperty(value = "上次上传时间")
    private Date lastUploadTime;

    @Column(name = "order_num")
    @ApiModelProperty(value = "排序号")
    private Integer orderNum;

    @Column(name = "view_count")
    @ApiModelProperty(value = "总浏览次数")
    private Integer viewCount;

    @Column(name = "yesterday_view")
    @ApiModelProperty(value = "昨日访问")
    private Integer yesterdayView;

    @Column(name = "character_count")
    @ApiModelProperty(value = "字符数")
    private Integer characterCount;

    @Column(name = "status")
    @ApiModelProperty(value = "状态")
    private Integer status;

    @Column(name = "update_time")
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    /**
     * 不参与序列化的字段，存储博客html文件的内部地址
     */
    private transient String filePath;

    /**
     * 对外公开的博客地址，blogPath依赖于filePath
     */
    private transient String blogPath;

    /**
     * 便于前端展示
     */
    private transient String blogDate;

    private transient String preBlog;

    private transient String preBlogTitle;

    private transient String nextBlog;

    private transient String nextBlogTitle;

    private transient Integer recommendValue;

    public void copy(SysBlog source) {
        BeanUtil.copyProperties(source, this, CopyOptions.create().setIgnoreNullValue(true));
    }

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
            if (!o1.createDate.equals(o2.createDate)) {
                return o1.createDate.compareTo(o2.createDate);
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
                int r = o2.createDate.compareTo(o1.createDate);
                if (r != 0) {
                    return r;
                }
                return o2.id.compareTo(o1.id);
            } else {
                return o2.lastUploadTime.compareTo(o1.lastUploadTime);
            }
        }
    }

    /**
     * 推荐值比较器，具体算法思路大致如下：创建时间影响占比0.1、总阅读量占比0.35、昨日阅读量占比0.15，更新时间占比0.4
     * 如果该项没有值则忽略，假设有如下数据
     * 序号 名称 创建时间 更新时间 昨日阅读量 总阅读量
     * 1   博客1 20200101 20200102 10    100
     * 2   博客2 20200202 20200220 200   200
     * 3   博客3 20200301 20200311 2     3
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
        double nDayAgo = 1, yesterdayView = 1, totalView = 1, characterCount = 1;
        if (this.createDate != null) {
            // 这个值越大，则推荐值越低，负相关
            nDayAgo = (DateUtil.parseIntDate(now) - this.createDate);
        }
        if (nDayAgo > 360) {
            nDayAgo /= 18.8 + 16;
        } else if (nDayAgo > 180) {
            nDayAgo /= 14.8 + 12;
        } else if (nDayAgo > 90) {
            nDayAgo /= 8.8 + 8;
        } else if (nDayAgo > 30) {
            nDayAgo /= 2.8 + 4;
        } else if (nDayAgo > 15) {
            nDayAgo = nDayAgo / 2 + 1;
        }
        // 再非实时计算的前提下，最终我们希望得到如下一个近似结果，总推荐值
        // 1. 跟创建天数成反比，创建天数越小推荐值越大，影响占比约为 50%
        // 2. 跟昨日访问和总访问成正比，访问越多推荐值越大，影响占比约为 5% 15%
        // 3. 跟字符数成正比，字数越多-代表写得越好，则推荐值越大，影响占比约为 30%
        // 4. 对于标注*的，一个 * 加200推荐值，总推荐值应固定在1500左右
        double value = 2150 - (nDayAgo * 1)  + yesterdayView / 3 + totalView / 4 + (characterCount / 76);
        if (this.title.contains("**")) {
            value += 320;
        } else if (this.title.contains("*")) {
            value += 125;
        }
        this.recommendValue = (int) value;
        return this.recommendValue;
    }
}