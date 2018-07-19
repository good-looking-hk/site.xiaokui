package site.xiaokui.module.sys.user.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import site.xiaokui.module.base.entity.BaseEntity;

/**
 * @author HK
 * @date 2018-05-25 16:32
 */
@ToString(callSuper = true)
@Getter@Setter
public class SysRole extends BaseEntity<SysRole> {

    private String description;

}
