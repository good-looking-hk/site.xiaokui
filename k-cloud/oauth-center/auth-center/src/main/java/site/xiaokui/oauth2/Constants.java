package site.xiaokui.oauth2;

/**
 * @author hk
 */
public class Constants {

    public static final String RESOURCE_SERVER_NAME = "oauth2-server";

    public static final String INVALID_CLIENT_DESCRIPTION = "客户端验证失败，错误的client_id/client_secret";

    /**
     * 对于认证code、授权token的缓存名称
     */
    public static final String OAUTH_CACHE_NAME = "site.xiaokui.oauth2-cache";

    /**
     * 认证code、授权token过期时间，默认为1小时
     */
    public static final int OAUTH_CACHE_EXPIRED_TIME_IN_SECOND = 60 * 60;
}
