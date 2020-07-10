package site.xiaokui.base.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import site.xiaokui.base.entity.ParentEntity;

import java.util.List;

/**
 * @author HK
 * @date 2018-05-25 16:32
 */
@ToString(callSuper = true)
@Getter@Setter
public class SysMenu extends ParentEntity {

    private String icon;

    private String url;

    /**
     * 菜单层级 1：一级菜单， 2：二级菜单， 3：三级菜单(页面按钮)
     */
    private Integer type;

    /**
     * 是否启用
     */
    private Integer enabled;

    /**
     * 子菜单集合，不可序列化
     */
    private transient List<SysMenu> list;
}
