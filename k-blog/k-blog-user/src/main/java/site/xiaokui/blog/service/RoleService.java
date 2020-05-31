package site.xiaokui.blog.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.xiaokui.base.service.BaseService;
import site.xiaokui.blog.dao.RoleMenuDao;
import site.xiaokui.blog.entity.SysRole;
import site.xiaokui.blog.entity.SysRoleMenu;

/**
 * @author HK
 * @date 2018-06-10 15:39
 */
@Service
public class RoleService extends BaseService<SysRole> {

    @Autowired
    private RoleMenuDao roleMenuDao;

    public boolean deleteMenuByRoleId(Long roleId) {
        return roleMenuDao.deleteMenuByRoleId(roleId) > 0;
    }

    /**
     * 为角色分配id
     *
     * @param roleId  角色id
     * @param menuIds 菜单ids
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean assignRoleMenu(Long roleId, Long[] menuIds) {
        deleteMenuByRoleId(roleId);
        for (Long temp : menuIds) {
            SysRoleMenu roleMenu = new SysRoleMenu();
            roleMenu.setMenuId(temp);
            roleMenu.setRoleId(roleId);
            roleMenuDao.insert(roleMenu, false);
        }
        return menuIds.length > 0;
    }
}
