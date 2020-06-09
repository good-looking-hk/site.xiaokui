package site.xiaokui.user.dao;

import org.beetl.sql.core.annotatoin.Param;
import org.beetl.sql.core.annotatoin.SqlResource;
import org.beetl.sql.core.mapper.BaseMapper;
import site.xiaokui.module.user.entity.SysRole;

/**
 * @author HK
 * @date 2018-05-25 21:07
 */
@SqlResource("sys.role")
public interface RoleDao extends BaseMapper<SysRole> {


    /**
     * 根据角色id返回角色名称
     * @param roleId 角色id
     * @return 角色名称
     */
    String getRoleNameByRoleId(@Param("roleId") Integer roleId);
}
