package site.xiaokui.module.weixin.rebot;

/**
 * @author HK
 * @date 2020-04-22 09:19
 */
public enum ErrorStatus {

    /**
     * 微信机器人返回状态码集合
     */
    INVALID_TOKEN(1001, "token无效"),

    NO_VERIFIED(1002, "机器人审核没有通过"),

    LOCK_USER_ID(1003, "签名缺少userid字段"),

    SIGN_IS_EMPTY(1004, "签名字段为空"),

    SIGN_IS_EXPIRED(1005, "签名过期或无效"),

    SIGN_FAILED(1006, "签名校验失败，缺少userid字段");

    int code;

    String msg;

    ErrorStatus(int code, String msg) {
        this.code =  code;
        this.msg = msg;
    }
}
