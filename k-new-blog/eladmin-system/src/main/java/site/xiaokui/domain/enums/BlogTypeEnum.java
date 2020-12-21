package site.xiaokui.domain.enums;

import lombok.Getter;

/**
 * @author HK
 * @date 2018-06-25 00:14
 */
public enum BlogTypeEnum {
    /**
     * 博客类型
     */
    PUBLIC("1", "公开"), PROTECTED("2", "受保护，密码访问"), PRIVATE("-1", "仅自己可见");

    @Getter
    String code;

    @Getter
    String msg;

    BlogTypeEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static String codeOf(String msg) {
        if (msg == null) {
            return null;
        }
        for (BlogTypeEnum m : BlogTypeEnum.values()) {
            if (m.getMsg().equals(msg)) {
                return m.getCode();
            }
        }
        return null;
    }
}
