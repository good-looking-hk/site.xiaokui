package site.xiaokui.blog;

/**
 * @author HK
 * @date 2018-06-30 13:45
 */
public class Constants {

    /**
     * 本地开放环境
     */
    public static final String PROFILE_LOCAL = "local";

    /**
     * 线上环境
     */
    public static final String PROFILE_REMOTE = "remote";

    /**
     * 本地测试环境，数据库为远程数据库
     */
    public static final String PROFILE_TEST = "test";

    public static final String BLOG_TYPE_PRI = "pri", BLOG_TYPE_PRO = "pro", BLOG_TYPE_PUB = "pub";

    public static final String BLOG_LAYOUT_DIR = "dir", BLOG_LAYOUT_TIME = "time";

    /**
     * 博客访问目录前缀
     */
    public static final String PREFIX = "/blog";

    /**
     * 博客管理前缀，默认/sys/blog
     */
    public static final String BLOG_PREFIX = "/sys" + PREFIX;

    /**
     * 音乐管理前缀
     */
    public static final String MUSIC_PREFIX = "/sys/music";

    /**
     * beetl对于博客文件的前缀标识
     */
    public static final String BLOG_START_FLAG = "blog:";

    public static final String FILE_START_FLAG = "file:";

    public static final String HTML_SUFFIX = ".html";

    public static final String MD_SUFFIX = ".md";

    /**
     * 字节为单位，默认4M
     */
    public static final long MAX_BLOG_UPLOAD_FILE = 1024 * 1024 * 4;
}
