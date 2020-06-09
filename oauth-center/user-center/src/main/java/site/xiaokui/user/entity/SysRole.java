package site.xiaokui.user.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import site.xiaokui.entity.ParentEntity;

/**
 * @author HK
 * @date 2018-05-25 16:32
 */
@ToString(callSuper = true)
@Getter@Setter
public class SysRole extends ParentEntity {

    private String description;

}
