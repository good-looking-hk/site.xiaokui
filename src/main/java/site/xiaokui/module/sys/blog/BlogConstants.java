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
     * 上传博客目录，注意，此项对应的是linux下的/xiaokui/upload文件夹，请确保有相应的访问权限
     */
    public static final String UPLOAD_PATH = "/xiaokui/upload/";

    /**
     * july的纯音乐
     */
    public static final String MUSIC_PATH = "/xiaokui/musics/music/july";

    public static final String BLOG_START_FLAG = "blog:";

    /**
     * 用户临时文件夹名称
     */
    public static final String TEMP_DIR = "/$temp/";

    public static final String SLASH = "/";

    public static final String HTML_SUFFIX = ".html";

    /**
     * 字节为单位，默认8M
     */
    public static final long MAX_UPLOAD_FILE = 1024 * 1024 * 6;

}
