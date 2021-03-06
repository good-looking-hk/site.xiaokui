package db.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author HK
 * @date 2021-03-17 17:49
 */
@Data
public class User implements Serializable {

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 1-男 2-女
     */
    private String sex;

    private Integer age;

    private String name;

    private String phone;

    private Date createTime;

    private Date modifiedTime;

    private static final long serialVersionUID = 1L;
}
