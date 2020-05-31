package site.xiaokui.blog.controller;

import cn.hutool.core.convert.Convert;
import cn.hutool.json.JSON;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import site.xiaokui.base.controller.ConstantController;
import site.xiaokui.base.entity.ResultEntity;
import site.xiaokui.base.entity.SysMenu;
import site.xiaokui.base.entity.ZTreeNode;
import site.xiaokui.base.util.StringUtil;
import site.xiaokui.blog.constant.UserConstants;
import site.xiaokui.blog.entity.SysRole;
import site.xiaokui.blog.entity.enums.RoleTypeEnum;
import site.xiaokui.blog.entity.wrapper.SysRoleWrapper;
import site.xiaokui.blog.service.MenuService;
import site.xiaokui.blog.service.RoleService;
import site.xiaokui.blog.service.ServiceFactory;
import site.xiaokui.blog.service.UserService;
import site.xiaokui.blog.util.ZTreeTool;

import java.util.Date;
import java.util.List;



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
    private UserService userService;

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
    @PostMapping(ConstantController.LIST)
    @ResponseBody
    public JSON list() {
        return new SysRoleWrapper(roleService.all()).toJson();
    }

    @RequiresPermissions(ROLE_PREFIX + ConstantController.ADD)
    @PostMapping(ConstantController.TREE)
    @ResponseBody
    public List<ZTreeNode> tree() {
        List<SysRole> list = roleService.all();
        return ZTreeTool.toZTreeNodeList(list);
    }

    @RequiresPermissions(ROLE_PREFIX + ConstantController.ADD)
    @PostMapping(ConstantController.ADD)
    @ResponseBody
    public ResultEntity add(String name, Long parentId, @RequestParam(required = false) String parentName, Integer orderNum, String description) {
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

    @RequiresPermissions(ROLE_PREFIX + ConstantController.EDIT)
    @GetMapping(ConstantController.EDIT + "/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        SysRole sysRole = roleService.getById(id);
        if (sysRole == null) {
            return ConstantController.ERROR;
        }
        model.addAttribute("role", sysRole);
        model.addAttribute("parentName", ServiceFactory.me().getRoleName(sysRole.getParentId().intValue()));
        return ROLE_PREFIX + ConstantController.EDIT;
    }

    @RequiresPermissions(ROLE_PREFIX + ConstantController.EDIT)
    @PostMapping(ConstantController.EDIT)
    @ResponseBody
    public ResultEntity edit(SysRole role) {
        boolean success = roleService.updateById(role);
        return returnResult(success);

    }

    @RequiresPermissions(ROLE_PREFIX + ConstantController.REMOVE)
    @PostMapping(ConstantController.REMOVE)
    @ResponseBody
    public ResultEntity remove(Integer id) {
        String roleName = RoleTypeEnum.valueOf(id);
        if (roleName != null) {
            return ResultEntity.error("不能删除系统内置用户");
        }
        boolean isInUse = userService.roleIdIsInUse(id);
        if (isInUse) {
            return this.error("该角色下用户不为空");
        }
        boolean success = roleService.deleteById(id);
        return returnResult(success);
    }

    @RequiresPermissions(ROLE_PREFIX + UserConstants.SET_AUTHORITY)
    @GetMapping(UserConstants.SET_AUTHORITY + "/{roleId}")
    public String setAuthority(@PathVariable Integer roleId, Model model) {
        model.addAttribute("roleId", roleId);
        model.addAttribute("roleName", ServiceFactory.me().getRoleName(roleId));
        return ROLE_PREFIX + UserConstants.SET_AUTHORITY;
    }

    /**
     * 配置权限
     */
    @RequiresPermissions(ROLE_PREFIX + UserConstants.SET_AUTHORITY)
    @PostMapping(UserConstants.SET_AUTHORITY)
    @ResponseBody
    public ResultEntity setAuthority(@RequestParam("roleId") Long roleId, @RequestParam("ids") String ids) {
        if (StringUtil.hasEmptyStrOrLessThanEqualsZeroNumber(roleId, ids)) {
            return ResultEntity.paramError();
        }
        Long[] id = Convert.toLongArray(StringUtil.split(ids, ","));
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
