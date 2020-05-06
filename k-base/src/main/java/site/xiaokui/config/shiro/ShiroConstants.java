package site.xiaokui.config.shiro;

import org.apache.shiro.subject.support.DefaultSubjectContext;

/**
 * 不声明访问关键字即为 包权限的
 *
 * @author HK
 * @date 2018-06-27 21:48
 */
public class ShiroConstants {

    /**
     * 默认为DefaultSubjectContext_PRINCIPALS_SESSION_KEY
     */
    public static final String SHIRO_USER_SESSION_KEY = DefaultSubjectContext.PRINCIPALS_SESSION_KEY;

    /**
     * shiro里面session缓存名称，需要与ehcache种保持一致
     */
    static final String SHIRO_ACTIVE_SESSION_CACHE_NAME = "hk-shiroActiveSessionCache";

    /**
     * 记录在线用户的挤出信息，前端根据此信息重新跳转到登录界面
     */
    static final String SHIRO_KICK_OUT_KEY = "forceOut";

    /**
     * 用户被挤出后的页面跳转地址
     */
    static final String SHIRO_KICK_OUT_URL = "/login?forceOut=true";

    /**
     * 默认为挤出之前登录的用户
     */
    static final boolean SHIRO_KICK_OUT_BEFORE_LOGIN = true;

    /**
     * 默认盐值长度
     */
    static final int DEFAULT_SALT_LENGTH = 10;

    // shiro常量

    /**
     * 具体请看{@link org.apache.shiro.web.filter.mgt.DefaultFilter}
     */
    static final String SHIRO_ANYBODY = "anon";

    static final String SHIRO_REMEMBER_ME = "user";

    static final String SHIRO_LOGIN_USER = "authc";
}
