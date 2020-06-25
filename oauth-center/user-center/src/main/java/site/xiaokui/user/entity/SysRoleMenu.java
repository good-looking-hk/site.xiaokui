package site.xiaokui.user.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author HK
 * @date 2018-05-26 21:30
 */
@Data
public class SysRoleMenu implements Serializable {

    private Integer id;

    private Long roleId;

    private Long menuId;

    private Date createTime;
}
