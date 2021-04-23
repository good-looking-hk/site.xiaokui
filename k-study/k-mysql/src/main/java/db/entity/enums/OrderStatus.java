package db.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author HK
 * @date 2021-04-21 17:31
 */
@RequiredArgsConstructor
@Getter
public enum OrderStatus {

    //
    PROCESSING(0, "处理中"),

    SUCCESS(1, "支付成功"),

    FAIL(2, "支付失败"),

    ;

    private final Integer status;
    private final String description;

    public static OrderStatus fromStatus(Integer status) {
        for (OrderStatus orderStatus : OrderStatus.values()) {
            if (orderStatus.getStatus().equals(status)) {
                return orderStatus;
            }
        }
        throw new IllegalArgumentException("status = " + status);
    }
}
