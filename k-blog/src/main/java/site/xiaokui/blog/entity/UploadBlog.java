package site.xiaokui.blog.entity;

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
    private String suffix;
    private Date createTime;
    private String errorInfo;
    private File uploadFile;

    /**
     * 对于md文件，length = 77428 或2926，文件实际大小
     * 对于html字符串，length = 61024 或 1710，html字符串大小
     * 对于MarkdownParser，textLength = 51021 或 1570，这个是最为真实的字符个数
     * 对于html文件，length = 80942 或 3282，文件实际大小
     * 因此，对于文章的字数统计，采用如下算法(假设n = 三者中的较小值，即MarkdownParser所统计的字符个数)，有
     *  n <= 1000, n = n * 0.94
     *  n <= 2000, n = n * 0.93
     *  n <= 5000, n = n * 0.92
     *  n > 5000, n = n * 0.9
     */
    private Integer characterCount;

    public static double determineCharCount(long htmlLength) {
        if (htmlLength <= 1000) {
            return htmlLength * 0.93;
        }
        if (htmlLength <= 2000) {
            return htmlLength * 0.91;
        }
        if (htmlLength <= 5000) {
            return htmlLength * 0.8;
        }
        return htmlLength * 0.75;
    }
}