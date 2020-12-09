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
package site.xiaokui.service.dto;

import lombok.Data;
import java.sql.Timestamp;
import java.io.Serializable;

/**
* @website https://el-admin.vip
* @description /
* @author hk
* @date 2020-12-01
**/
@Data
public class SysBlogDto implements Serializable {

    /** 博客id */
    private Long id;

    /** 博客目录 */
    private String dir;

    /** 博客标题 */
    private String title;

    /** 博客文件名称 */
    private String fileName;

    /** 拥有者id */
    private Integer userId;

    /** 博客类型 */
    private String blogType;

    /** 创建时间 */
    private Timestamp createTime;

    /** 上次上传时间 */
    private Timestamp lastUploadTime;

    /** 排序号 */
    private Integer orderNum;

    /** 总浏览次数 */
    private Integer viewCount;

    /** 昨日访问 */
    private Integer yesterdayView;

    /** 字符数 */
    private Integer characterCount;

    /** 状态 */
    private Integer status;

    /** 更新时间 */
    private Timestamp updateTime;

    /**
     * 对外公开的博客地址，blogPath依赖于filePath
     */
    private transient String blogPath;

    private transient String preBlog;

    private transient String preBlogTitle;

    private transient String nextBlog;

    private transient String nextBlogTitle;

    /**
     * 推荐值
     */
    private int recommendValue;
}