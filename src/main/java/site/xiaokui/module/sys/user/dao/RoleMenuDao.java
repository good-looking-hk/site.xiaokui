package site.xiaokui.module.sys.user.dao;

import org.beetl.sql.core.annotatoin.Param;
import org.beetl.sql.core.annotatoin.SqlResource;
import org.beetl.sql.core.mapper.BaseMapper;
import site.xiaokui.module.sys.user.entity.SysMenu;
import site.xiaokui.module.sys.user.entity.SysRoleMenu;
import site.xiaokui.module.sys.user.service.ServiceFactory;

/**
 * @author HK
 * @date 2018-06-12 20:03
 */
@SqlResource("sys.role_menu")
public interface RoleMenuDao extends BaseMapper<SysRoleMenu> {

    /**
     * 删除角色的所有菜单
     * @param roleId 角色菜单
     * @return 所影响的行数
     */
    int deleteMenuByRoleId(@Param("roleId") Integer roleId);
}
