package site.xiaokui.module.sys.blog.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.File;
import java.util.Date;

/**
 * @author HK
 * @date 2018-06-26 20:38
 */
@ToString
@Getter@Setter
public class UploadBlog {
    private String blogSpace;
    private String name;
    private String dir;
    private Integer orderNum;
    private Date createTime;
    private String errorInfo;
    private File uploadFile;
}