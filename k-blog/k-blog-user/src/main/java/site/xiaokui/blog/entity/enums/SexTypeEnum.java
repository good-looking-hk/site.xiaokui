package site.xiaokui.blog.entity.enums;

import lombok.Getter;
import lombok.Setter;

/**
 * @author HK
 * @date 2018-06-14 16:25
 */
public enum SexTypeEnum {
    /**
     * 可以替换为字典
     */
    MALE(1, "男"), FEMALE(2, "女"), UNKNOWN(3, "保密");

    @Getter@Setter
    Integer code;

    @Getter@Setter
    String msg;

    SexTypeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static Integer codeOf(String msg) {
        if (msg == null) {
            return null;
        }
        for (SexTypeEnum m : SexTypeEnum.values()) {
            if (m.getMsg().equals(msg)) {
                return m.getCode();
            }
        }
        return null;
    }

    public static String valueOf(Integer code) {
        if (code == null) {
            return null;
        }
        for (SexTypeEnum m : SexTypeEnum.values()) {
            if (m.getCode().equals(code)) {
                return m.getMsg();
            }
        }
        return null;
    }
}
