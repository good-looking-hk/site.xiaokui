package site.xiaokui.entity.enums;

import lombok.Getter;
import lombok.Setter;

/**
 * @author HK
 * @date 2018-05-27 17:47
 */
public enum MenuTypeEnum {
    /**
     * 用户手动添加菜单
     */
    ZERO(0, "顶级"), FIRST(1, "一级菜单"), SECOND(2, "二级菜单"), THIRD(3, "页面菜单"),

    /**
     * 系统内置菜单，不可删除
     */
    SYS_FIRST(4, "一级菜单"), SYS_SECOND(5, "二级菜单"), SYS_THIRD(6, "页面菜单");

    @Getter@Setter
    int code;

    @Getter@Setter
    String message;

    MenuTypeEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
