package site.xiaokui.blog.constant;

/**
 * @author HK
 * @date 2018-05-29 10:52
 */
public class UserConstants {

    // ============User Constant==========

    public static final String REMEMBER_FLAG = "on";

    public static final String DEFAULT_HEAD_IMG = "/img/rabbit.gif";

    public static final String DEFAULT_DESCRIPTION = "我最萌，哼";


    // ============Role Constant==========

    public static final String SET_AUTHORITY = "/setAuthority";

    /**
     * 是否强制删除菜单（一二级菜单）
     */
    public static final boolean IS_FOCUS_DELETE_MENU = false;


    // ============Menu Constant==========

    public static final String ICON_START_FLAG = "fa fa-";

    public static final String SYS_PREFIX = "/sys";

    public static final String USER_PREFIX = SYS_PREFIX + "/user";

    public static final String DEPT_PREFIX = SYS_PREFIX + "/dept";

    public static final String MENU_PREFIX = SYS_PREFIX + "/menu";

    public static final String ROLE_PREFIX = SYS_PREFIX + "/role";

    public static final String ONLINE_PREFIX = SYS_PREFIX + "/online";
}
