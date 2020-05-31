package site.xiaokui.blog.entity.enums;

import lombok.Getter;

/**
 * @author HK
 * @date 2018-05-30 17:27
 */
public enum MenuStatusEnum {
    /**
     * 可以替换为字典
     */
    NORMAL(1, "启用"), DISABLED(0, "禁用");

    @Getter
    int code;
    @Getter
    String msg;

    MenuStatusEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static Integer codeOf(String msg) {
        if (msg == null) {
            return null;
        }
        for (MenuStatusEnum m : MenuStatusEnum.values()) {
            if (m.getMsg().equals(msg)) {
                return m.getCode();
            }
        }
        return null;
    }
}
