package site.xiaokui.module.sys.blog;

/**
 * @author HK
 * @date 2019-02-21 14:05
 */
public class RedisKey {

    /**
     * 最近访问后缀，key自动过期
     */
    public static final String KEY_RECENT_UPLOAD_SUFFIX = "_krus";

    /**
     * 最多访问后缀--单个用户博客，默认只取top10
     */
    public static final String KEY_MOST_VIEW_SUFFIX = "_most_view_list";

    /**
     * 记录博客访问ip后缀，需要匹配删除
     */
    public static final String HASH_IP_VIEWS_SUFFIX = "blog_view_ip_list";

    /**
     * 博客访问次数--所有博客
     */
    public static final String HASH_BLOG_VIEW_COUNT = "blog_view_map";

    /**
     * 黑名单，不能增加阅读量，直接删除key即可
     */
    public static final String KEY_BLACK_VIEW_IP = "black_view_list";
}
