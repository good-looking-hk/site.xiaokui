package site.xiaokui.user.entity.enums;

import lombok.Getter;
import lombok.Setter;

/**
 * @author HK
 * @date 2018-12-07 21:53
 */
public enum DeptTypeEnum {
    /**
     * 普通用户
     */
    NONE(0, "none");

    @Getter
    @Setter
    int code;

    @Getter@Setter
    String msg;

    DeptTypeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static String valueOf(Integer code) {
        if (code == null) {
            return null;
        }
        for (DeptTypeEnum m : DeptTypeEnum.values()) {
            if (m.getCode() == code) {
                return m.getMsg();
            }
        }
        return null;
    }
}
