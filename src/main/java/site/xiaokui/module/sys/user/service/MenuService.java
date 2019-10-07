package site.xiaokui.module.sys.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.xiaokui.common.util.StringUtil;
import site.xiaokui.module.base.service.BaseService;
import site.xiaokui.module.sys.user.entity.enums.MenuStatusEnum;
import site.xiaokui.module.base.enums.MenuTypeEnum;
import site.xiaokui.module.sys.user.dao.MenuDao;
import site.xiaokui.module.sys.user.dao.RoleMenuDao;
import site.xiaokui.module.sys.user.entity.SysMenu;
import site.xiaokui.module.sys.user.entity.SysRoleMenu;

import java.util.*;

/**
 * @author HK
 * @date 2018-05-28 20:07
 */
@Service
public class MenuService extends BaseService<SysMenu> {

    @Autowired
    private MenuDao menuDao;

    @Autowired
    private RoleMenuDao roleMenuDao;

    @Transactional(rollbackFor = Exception.class)
    public boolean insertRoleMenu(Integer roleId, SysMenu menu) {
        boolean result = insertIgnoreNullReturnKey(menu);
        SysRoleMenu sysRoleMenu = new SysRoleMenu();
        sysRoleMenu.setMenuId(menu.getId());
        sysRoleMenu.setRoleId(roleId);
        sysRoleMenu.setCreateTime(new Date());
        roleMenuDao.insert(sysRoleMenu);
        return result;
    }

    /**
     * 根据角色id获取对应的菜单，用于构造菜单树（只包括1,2级菜单，不含3级菜单和禁用的菜单）
     * 移除特定匹配项的一个可选方案
     * list.removeIf(test->test.getType().equals(MenuTypeEnum.THIRD.getCode()));
     */
    public List<SysMenu> listMenuTree(Integer roleId) {
        List<SysMenu> list = this.listMenuByRoleId(roleId);
        list.removeIf(test->test.getType() == MenuTypeEnum.THIRD.getCode() || test.getEnabled() == MenuStatusEnum.DISABLED.getCode());
        return list;
    }


    public List<SysMenu> listMenuByRoleId(Integer roleId) {
        return menuDao.listMenuByRoleId(roleId);
    }

    public List<String> findPermissionsByRoleId(Integer roleId) {
        return menuDao.findPermissionsByRoleId(roleId);
    }

    /**
     * 根据角色id返回一个格式化的菜单列表（二级菜单嵌套在一级菜单中），用于在首页显示可用菜单
     * @param roleId 角色id
     * @return 格式化好的用户界面菜单列表
     */
    public List<SysMenu> getUserMenu(Integer roleId) {
        List<SysMenu> menuList = menuDao.listMenuByRoleId(roleId);
        return resolveMenuList(menuList);
    }

    public boolean deleteMenu(Integer menuId) {
        return deleteMenu(menuId, false);
    }

    /**
     * 删除菜单
     * @param menuId 菜单id
     * @param focus 是否强制删除（包括其子菜单）
     * @return 是否成功
     */
    public boolean deleteMenu(Integer menuId, boolean focus) {
        SysMenu sysMenu = this.getById(menuId);
        // 如果是普通页面菜单(最低级)，直接删除
        if (MenuTypeEnum.THIRD.getCode() == sysMenu.getType()) {
            return this.deleteById(menuId);
        }
        // 如果是有子菜单，是否强制删除
        if (focus) {
            SysMenu temp = new SysMenu();
            temp.setParentId(menuId);
            List<SysMenu> list = this.match(temp);
            if (list != null) {
                for (SysMenu s : list) {
                    this.deleteById(s);
                }
            }
            return true;
        } else {
            SysMenu temp = new SysMenu();
            temp.setParentId(menuId);
            List<SysMenu> list = this.match(temp);
            if (list == null || list.size() == 0) {
                this.deleteById(menuId);
                return true;
            }
        }
        return false;
    }

    private static List<SysMenu> resolveMenuList(List<SysMenu> menuList) {
        // 调用自定义排序方法，排序字段依次为parentId > orderNum > id
        Collections.sort(menuList);
        Iterator<SysMenu> it = menuList.iterator();
        while (it.hasNext()) {
            SysMenu temp = it.next();
            // 如果是页面菜单，直接移除
            if (temp.getType() == MenuTypeEnum.THIRD.getCode()) {
                it.remove();
                continue;
            }
            if (temp.getType() == MenuTypeEnum.FIRST.getCode()) {
                // 如果是一级菜单，即包含子菜单
                if (temp.getList() == null) {
                    temp.setList(new ArrayList<>());
                }
            }
            if (temp.getType() == MenuTypeEnum.SECOND.getCode()) {
                // 如果是二级菜单，则把其添加至对应的一级父菜单的子菜单列表中
                SysMenu menu = new SysMenu();
                menu.setName(temp.getName());
                menu.setType(temp.getType());
                menu.setIcon(temp.getIcon());
                menu.setUrl(temp.getUrl());
                Integer parentMenuId = temp.getParentId();
                // 添加至父菜单中
                SysMenu parent = findMenuByParentId(menuList, parentMenuId);
                if (parent != null) {
                    if (parent.getList() == null) {
                        parent.setList(new ArrayList<>());
                    }
                    parent.getList().add(menu);
                }
                // 移除原有菜单项
                it.remove();
            }
        }
        return menuList;
    }

    /**
     * 寻找二级菜单的父级菜单的子菜单列表
     */
    private static SysMenu findMenuByParentId(List<SysMenu> menuList, Integer parentId) {
        for (SysMenu menu : menuList) {
            if (menu.getId().equals(parentId)) {
                return menu;
            }
        }
        return null;
    }
}
