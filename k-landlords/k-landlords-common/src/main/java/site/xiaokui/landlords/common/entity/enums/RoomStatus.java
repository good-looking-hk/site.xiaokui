package site.xiaokui.landlords.common.entity.enums;

/**
 * @author HK
 * @date 2020-08-06 10:36
 */
public enum  RoomStatus {

    /**
     * 房间状态
     */
    BLANK("空闲"),

    WAIT("等待"),

    STARTING("开始");

    private String msg;

    private RoomStatus(String msg) {
        this.msg = msg;
    }

    public final String getMsg() {
        return msg;
    }

    public final void setMsg(String msg) {
        this.msg = msg;
    }
}
