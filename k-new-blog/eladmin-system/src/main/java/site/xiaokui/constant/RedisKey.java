package site.xiaokui.constant;

/**
 * @author HK
 * @date 2019-02-21 14:05
 */
public class RedisKey {

    /**
     * 单个用户博客最多访问有序集合后缀
     */
    public static final String USER_BLOG_VIEW_COUNT_SORT_MAP_SUFFIX = "_user_blog_view_count_sort_map";

    /**
     * 用户或IP地址对所有博客贡献访问量MAP
     */
    public static final String USER_OR_IP_CONTRIBUTE_VIEW_COUNT_MAP  = "user_or_ip_contribute_view_count_map";

    /**
     * 用户或IP对单篇博客的贡献访问量后缀
     */
    public static final String USER_OR_IP_TO_BLOG_CONTRIBUTE_VIEW_COUNT_SUFFIX = "_user_or_ip_to_blog_contribute_view_count_map";
}
