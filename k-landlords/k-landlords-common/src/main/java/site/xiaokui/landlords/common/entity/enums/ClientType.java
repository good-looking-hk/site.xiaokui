package site.xiaokui.landlords.common.entity.enums;

/**
 * @author HK
 * @date 2020-08-03 17:14
 */
public enum  ClientType {

    /**
     * 客服端类型，农民或地主
     */
    LANDLORD("地主"),

    PEASANT("农民");

    private String msg;

    ClientType(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
