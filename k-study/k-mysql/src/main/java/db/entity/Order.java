package db.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * @author HK
 * @date 2021-04-21 17:27
 */
@Data
public class Order {

    private Long id;
    private String creator;
    private String editor;

    private OffsetDateTime createTime;
    private OffsetDateTime editTime;
    private Long version;
    private Integer deleted;

    private String orderId;
    private BigDecimal amount;
    private OffsetDateTime paymentTime;

    private Integer orderStatus;
}
