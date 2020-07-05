package site.xiaokui.enums;

/**
 * @author HK
 * @date 2020-07-04 21:03
 */
public enum OrderStatus {

    /**
     * 订单状态
     */
    TO_PAY(0, "待支付"), PAID(1, "支付成功"), PAID_FAIL(2, "支付失败"), REFUND(3, "退款订单");

    int code;

    String desc;

    OrderStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static String convertCode(int code) {
        for (OrderStatus o : values()) {
            if (o.getCode() == code) {
                return o.getDesc();
            }
        }
        throw new RuntimeException("非法转换code:" + code);
    }
}
