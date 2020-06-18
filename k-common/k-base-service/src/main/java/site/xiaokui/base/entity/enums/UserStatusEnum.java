package site.xiaokui.base.entity.enums;

import lombok.Getter;
import lombok.Setter;

/**
 * @author HK
 * @date 2018-05-23 21:47
 */
public enum UserStatusEnum {

    /**
     * 用户状态字典
     */
    OK(1, "启用"), FROZEN(2, "冻结"), DELETED(3, "删除");

    @Getter@Setter
    int code;

    @Getter@Setter
    String message;

    UserStatusEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public static String valueOf(Integer value) {
        if (value == null) {
            return null;
        } else {
            for (UserStatusEnum ms : UserStatusEnum.values()) {
                if (ms.getCode() == value) {
                    return ms.getMessage();
                }
            }
            return null;
        }
    }
}
