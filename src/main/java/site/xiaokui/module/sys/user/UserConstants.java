package site.xiaokui.module.sys.user;

import org.apache.shiro.subject.support.DefaultSubjectContext;

/**
 * @author HK
 * @date 2018-05-29 10:52
 */
public class UserConstants {

    // ============User Constant==========

    public static final String REMEMBER_FLAT = "on";

    public static final int DEFAULT_SALT_LENGTH = 10;

    public static final String DEFAULT_HEAD_IMG = "/img/rabbit.gif";

    public static final String DEFAULT_DESCRIPTION = "我最萌，哼";


    // ============Role Constant==========

    public static final String SET_AUTHORITY = "/setAuthority";


    // ============Menu Constant==========

    public static final String ICON_START_FLAG = "fa fa-";

    public static final String SYS_PREFIX = "/sys";

    public static final String USER_PREFIX = SYS_PREFIX + "/user";

    public static final String DEPT_PREFIX = SYS_PREFIX + "/dept";

    public static final String MENU_PREFIX = SYS_PREFIX + "/menu";

    public static final String ROLE_PREFIX = SYS_PREFIX + "/role";

    public static final String ONLINE_PREFIX = SYS_PREFIX + "/online";
}
