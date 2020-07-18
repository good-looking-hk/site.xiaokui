package site.xiaokui.user.entity.enums;

import lombok.Getter;
import lombok.Setter;

/**
 * @author HK
 * @date 2018-06-22 22:46
 */
public enum  RoleTypeEnum {
    /**
     * 系统内置角色，不可删除
     * 顶级菜单只是一个标志，不参与数据库的存储
     */
    SUPER_ADMIN(1, "root"), ADMIN(2, "admin"), USER(3, "user"), GUEST(4, "guest");

    @Getter@Setter
    int code;

    @Getter@Setter
    String msg;

    RoleTypeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static String valueOf(Integer code) {
        if (code == null) {
            return null;
        }
        for (RoleTypeEnum m : RoleTypeEnum.values()) {
            if (m.getCode() == code) {
                return m.getMsg();
            }
        }
        return null;
    }
}
