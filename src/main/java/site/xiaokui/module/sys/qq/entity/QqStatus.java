package site.xiaokui.module.sys.qq.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * @author HK
 * @date 2019-02-14 13:48
 */
public enum  QqStatus {
    /**
     * 在线状态
     */
    ONLINE(1, "online"), HIDE(2, "hide"), OFFLINE(3, "offline");

    @Getter
    @Setter
    int code;

    @Getter@Setter
    String msg;

    QqStatus(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static String valueOf(Integer code) {
        if (code == null) {
            return null;
        }
        for (QqStatus m : QqStatus.values()) {
            if (m.getCode() == code) {
                return m.getMsg();
            }
        }
        return null;
    }
}
