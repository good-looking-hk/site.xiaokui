package site.xiaokui.module.sys.blog.entity;

import lombok.Getter;
import site.xiaokui.module.sys.user.entity.enums.MenuStatusEnum;

/**
 * @author HK
 * @date 2018-06-25 00:14
 */
public enum BlogStatusEnum {
    /**
     *
     */
    PUBLIC(1, "公开"), PROTECTED(2, "受保护，密码访问");

    @Getter
    int code;

    @Getter
    String msg;

    BlogStatusEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static Integer codeOf(String msg) {
        if (msg == null) {
            return null;
        }
        for (BlogStatusEnum m : BlogStatusEnum.values()) {
            if (m.getMsg().equals(msg)) {
                return m.getCode();
            }
        }
        return null;
    }
}
