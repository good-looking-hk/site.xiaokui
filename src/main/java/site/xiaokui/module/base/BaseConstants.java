package site.xiaokui.module.base;

/**
 * 系统基础常量，一般情况下，不建议修改
 * @author HK
 * @date 2018-06-27 20:10
 */
public class BaseConstants {

    // 系统内置角色

    public static final String SUPER_ADMIN = "root";

    public static final String ADMIN = "admin";

    public static final String USER = "user";

    public static final String GUEST = "guest";

    // 默认profile

    public static final String PROFILE_LOCAL = "local";

    public static final String PROFILE_REMOTE = "remote";

    public static final String PROFILE_WIN = "win";

    public static final String PROFILE_TEST = "test";


    // shiro常量

    /**
     * 具体请看{@link org.apache.shiro.web.filter.mgt.DefaultFilter}
     */
    public static final String ANYBODY = "anon";

    public static final String REMEMBER_ME = "user";

    public static final String LOGIN_USER = "authc";


    // 关于项目的一些规范命名

    public static final String TEMP_DIR = "/$temp/";

    public static final String SLASH = "/";
}
