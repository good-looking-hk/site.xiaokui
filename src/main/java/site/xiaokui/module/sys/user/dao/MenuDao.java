package site.xiaokui.module.sys.user.dao;

import org.beetl.sql.core.annotatoin.Param;
import org.beetl.sql.core.annotatoin.SqlResource;
import org.beetl.sql.core.mapper.BaseMapper;
import site.xiaokui.module.sys.user.entity.SysMenu;

import java.util.List;

/**
 * @author HK
 * @date 2018-05-25 21:06
 */
@SqlResource("sys.menu")
public interface MenuDao extends BaseMapper<SysMenu> {

    /**
     * 根据角色id查找菜单（包括所有菜单）
     * @param roleId 角色id
     * @return 菜单列表
     */
    List<SysMenu> listMenuByRoleId(@Param("roleId") Integer roleId);

    /**
     * 根据角色id查找用户权限
     * @param roleId 角色id
     * @return 权限列表
     */
    List<String> findPermissionsByRoleId(@Param("roleId") Integer roleId);
}
