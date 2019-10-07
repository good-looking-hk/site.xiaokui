package site.xiaokui.module.sys.blog;

/**
 * @author HK
 * @date 2018-06-30 13:45
 */
public class BlogConstants {

    /**
     * 博客访问目录前缀
     */
    public static final String PREFIX = "/blog";

    /**
     * 博客管理前缀
     */
    public static final String BLOG_PREFIX = "/sys" + PREFIX;

    /**
     * 音乐管理前缀
     */
    public static final String MUSIC_PREFIX = "/sys/music";


    public static final String BLOG_START_FLAG = "blog:";

    public static final String HTML_SUFFIX = ".html";

    /**
     * 字节为单位，默认8M
     */
    public static final long MAX_BLOG_UPLOAD_FILE = 1024 * 1024 * 8;

}
