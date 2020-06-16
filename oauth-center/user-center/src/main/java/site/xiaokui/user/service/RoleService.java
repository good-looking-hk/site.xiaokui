package site.xiaokui.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.xiaokui.base.service.BaseService;
import site.xiaokui.user.dao.RoleMenuDao;
import site.xiaokui.user.entity.SysRole;
import site.xiaokui.user.entity.SysRoleMenu;

/**
 * @author HK
 * @date 2018-06-10 15:39
 */
@Service
public class RoleService extends BaseService<SysRole> {

    @Autowired
    private RoleMenuDao roleMenuDao;

    public boolean deleteMenuByRoleId(Integer roleId) {
        return roleMenuDao.deleteMenuByRoleId(roleId) > 0;
    }

    /**
     * 为角色分配id
     *
     * @param roleId  角色id
     * @param menuIds 菜单ids
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean assignRoleMenu(Integer roleId, Integer[] menuIds) {
        deleteMenuByRoleId(roleId);
        for (int temp : menuIds) {
            SysRoleMenu roleMenu = new SysRoleMenu();
            roleMenu.setMenuId(temp);
            roleMenu.setRoleId(roleId);
            roleMenuDao.insert(roleMenu, false);
        }
        return menuIds.length > 0;
    }
}
