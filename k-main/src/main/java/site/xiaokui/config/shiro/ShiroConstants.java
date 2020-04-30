package site.xiaokui.config.shiro;

import org.apache.shiro.subject.support.DefaultSubjectContext;

/**
 * @author HK
 * @date 2018-06-27 21:48
 */
public class ShiroConstants {

    /**
     * shiro里面session缓存名称，需要与ehcache种保持一致
     */
    public static final String SHIRO_ACTIVE_SESSION_CACHE_NAME = "hk-shiroActiveSessionCache";

    /**
     * 默认为DefaultSubjectContext_PRINCIPALS_SESSION_KEY
     */
    public static final String SHIRO_USER_SESSION_KEY = DefaultSubjectContext.PRINCIPALS_SESSION_KEY;
}
