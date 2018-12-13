package site.xiaokui.module.sys.user.controller;

import cn.hutool.core.convert.Convert;
import cn.hutool.json.JSON;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import site.xiaokui.module.sys.user.UserConstants;
import site.xiaokui.module.base.controller.AbstractController;
import site.xiaokui.module.base.enums.RoleTypeEnum;
import site.xiaokui.common.util.StringUtil;
import site.xiaokui.module.base.entity.ResultEntity;
import site.xiaokui.module.sys.user.entity.SysMenu;
import site.xiaokui.module.sys.user.entity.SysRole;
import site.xiaokui.module.sys.user.entity.ZTreeNode;
import site.xiaokui.module.sys.user.entity.wrapper.SysRoleWrapper;
import site.xiaokui.module.sys.user.service.MenuService;
import site.xiaokui.module.sys.user.service.RoleService;
import site.xiaokui.module.sys.user.service.ServiceFactory;
import site.xiaokui.module.sys.user.util.ZTreeTool;

import java.util.Date;
import java.util.List;

import static site.xiaokui.module.sys.user.UserConstants.SET_AUTHORITY;

/**
 * @author HK
 * @date 2018-05-24 22:00
 */
@Controller
@RequestMapping(UserConstants.ROLE_PREFIX)
public class RoleController extends AbstractController {
    /**
     * ROLE_PREFIX字段默认为 /sys/role
     */
    private static final String ROLE_PREFIX = UserConstants.ROLE_PREFIX;

    @Autowired
    private RoleService roleService;

    @Autowired
    private MenuService menuService;

    @Override
    protected String setPrefix() {
        return ROLE_PREFIX;
    }

    /**
     * 获取角色列表
     */
    @RequiresPermissions(ROLE_PREFIX)
    @PostMapping(LIST)
    @ResponseBody
    public JSON list() {
        return new SysRoleWrapper(roleService.all()).toJson();
    }

    @RequiresPermissions(ROLE_PREFIX + ADD)
    @PostMapping(TREE)
    @ResponseBody
    public List<ZTreeNode> tree() {
        List<SysRole> list = roleService.all();
        return ZTreeTool.toZTreeNodeList(list);
    }

    @RequiresPermissions(ROLE_PREFIX + ADD)
    @PostMapping(ADD)
    @ResponseBody
    public ResultEntity add(String name, Integer parentId, @RequestParam(required = false) String parentName, Integer orderNum, String description) {
        if (this.isEmpty(name)) {
            return this.paramError(name);
        }
        SysRole parent = roleService.getById(parentId);
        if (parent == null) {
            return this.paramError(parentId);
        }
        if (parentName != null) {
            if (!parent.getName().equals(parentName)) {
                return this.paramError(parentName);
            }
        }
        SysRole sysRole = new SysRole();
        sysRole.setName(name);
        sysRole.setParentId(parentId);
        sysRole.setOrderNum(orderNum);
        sysRole.setCreateTime(new Date());
        boolean success = roleService.insertIgnoreNull(sysRole);
        return returnResult(success, "添加角色失败");
    }

    @RequiresPermissions(ROLE_PREFIX + EDIT)
    @GetMapping(EDIT + "/{id}")
    @Override
    public String edit(@PathVariable Integer id, Model model) {
        SysRole sysRole = roleService.getById(id);
        if (sysRole == null) {
            return ERROR;
        }
        model.addAttribute("role", sysRole);
        model.addAttribute("parentName", ServiceFactory.me().getRoleName(sysRole.getParentId()));
        return ROLE_PREFIX + EDIT;
    }

    @RequiresPermissions(ROLE_PREFIX + EDIT)
    @PostMapping(EDIT)
    @ResponseBody
    public ResultEntity edit(SysRole role) {
        role.setModifiedTime(new Date());
        boolean success = roleService.updateById(role);
        return returnResult(success);

    }

    @RequiresPermissions(ROLE_PREFIX + REMOVE)
    @PostMapping(REMOVE)
    @ResponseBody
    @Override
    public ResultEntity remove(Integer id) {
        String roleName = RoleTypeEnum.valueOf(id);
        if (roleName != null) {
            return ResultEntity.error("不能删除系统内置用户");
        }
        boolean success = roleService.deleteById(id);
        return returnResult(success);
    }

    @RequiresPermissions(ROLE_PREFIX + SET_AUTHORITY)
    @GetMapping(SET_AUTHORITY + "/{roleId}")
    public String setAuthority(@PathVariable Integer roleId, Model model) {
        model.addAttribute("roleId", roleId);
        model.addAttribute("roleName", ServiceFactory.me().getRoleName(roleId));
        return ROLE_PREFIX + SET_AUTHORITY;
    }

    /**
     * 配置权限
     */
    @RequiresPermissions(ROLE_PREFIX + SET_AUTHORITY)
    @PostMapping(SET_AUTHORITY)
    @ResponseBody
    public ResultEntity setAuthority(@RequestParam("roleId") Integer roleId, @RequestParam("ids") String ids) {
        if (StringUtil.hasEmptyStrOrLessThanEqualsZeroNumber(roleId, ids)) {
            return ResultEntity.paramError();
        }
        Integer[] id = Convert.toIntArray(StringUtil.split(ids, ","));
        boolean success = roleService.assignRoleMenu(roleId, id);
        return returnResult(success);
    }

    /**
     * 获取菜单列表
     */
    @RequiresPermissions(ROLE_PREFIX + "/setAuthority")
    @RequestMapping("/menuTree/{roleId}")
    @ResponseBody
    public List<ZTreeNode> menuTree(@PathVariable("roleId") Integer roleId) {
        List<SysMenu> roleMenu = menuService.listMenuByRoleId(roleId);
        List<SysMenu> allMenu = menuService.all();
        return ZTreeTool.findRoleMenuTree(roleMenu, allMenu);
    }
}