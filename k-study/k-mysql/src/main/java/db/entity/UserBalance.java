package db.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author HK
 * @date 2021-03-17 17:50
 */
@Data
public class UserBalance implements Serializable {

    private Long userId;

    private BigDecimal userBalance;

    private static final long serialVersionUID = 1L;
}
