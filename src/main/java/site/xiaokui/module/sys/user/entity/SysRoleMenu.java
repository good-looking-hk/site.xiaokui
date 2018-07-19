package site.xiaokui.module.sys.user.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author HK
 * @date 2018-05-26 21:30
 */
@Data
public class SysRoleMenu implements Serializable {

    private Integer id;

    private Integer roleId;

    private Integer menuId;
}
