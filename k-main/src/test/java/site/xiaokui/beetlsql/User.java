package site.xiaokui.beetlsql;


import lombok.Data;

import java.util.Date;

/**
 * @author HK
 * @date 2018-05-24 00:16
 */
@Data
public class User {

    private Integer id;

    private Integer age;

    //用户角色
    private Integer roleId;

    private String name;

    //用户名称
    private String userName;

    private Date createDate;

}