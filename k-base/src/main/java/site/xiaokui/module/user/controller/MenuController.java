package site.xiaokui.module.user.controller;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import site.xiaokui.common.util.StringUtil;
import site.xiaokui.controller.AbstractController;
import site.xiaokui.entity.ResultEntity;
import site.xiaokui.entity.ZTreeNode;
import site.xiaokui.entity.enums.MenuTypeEnum;
import site.xiaokui.module.user.UserConstants;
import site.xiaokui.module.user.entity.SysMenu;
import site.xiaokui.module.user.entity.enums.MenuStatusEnum;
import site.xiaokui.module.user.service.MenuService;
import site.xiaokui.module.user.util.ZTreeTool;

import java.util.*;

import static site.xiaokui.module.user.UserConstants.ICON_START_FLAG;


/**
 * @author HK
 * @date 2018-05-24 22:00
 */
@Controller
@RequestMapping(UserConstants.MENU_PREFIX)
public class MenuController extends AbstractController {
    /**
     * MENU_PREFIX字段默认为 /sys/menu
     */
    private static final String MENU_PREFIX = UserConstants.MENU_PREFIX;

    @Autowired
    private MenuService menuService;

    @Override
    protected String setPrefix() {
        return MENU_PREFIX;
    }

    /**
     * 获取对应的菜单，含参数的调用均为客户端的异步查找
     */
    @RequiresPermissions(MENU_PREFIX)
    @PostMapping(LIST)
    @ResponseBody
    public List<SysMenu> list(@RequestParam(required = false) String name, @RequestParam(required = false) String code,
                              @RequestParam(required = false) String url) {
        List<SysMenu> list;
        if (StringUtil.isAllEmpty(name, code, url)) {
            list = menuService.all();
            Collections.sort(list);
            return list;
        }

        Map<String, Object> map = new HashMap<>(4);
        if (StringUtil.isNotEmpty(name)) {
            map.put("name", name);
        }
        if (StringUtil.isNotEmpty(code)) {
            map.put("code", code);
        }
        if (StringUtil.isNotEmpty(url)) {
            map.put("url", url);
        }
        list = menuService.fuzzyQuery(map);
        Collections.sort(list);
        return list;
    }

    /**
     * 虽然可以简单地通过把SysMenu menu作为参数，但这样不能保证细粒度的控制（比如客户端胡乱填写信息），
     * 因此这里稍稍增加一点开销，以保证数据的正确性。
     * 添加菜单一定需要以下5个参数
     *
     * @param name       菜单名称
     * @param parentId   父菜单id
     * @param orderNum   排序号
     * @param url        url地址
     *                   <p>
     *                   对于另外两个非必需参数，添加者自动拥有该权限
     * @param icon       图标 当且只当该菜单为一级菜单时生效，可以没有
     * @param parentName 父菜单名称  用于验证父菜单名称，判断客服端是否随意篡改提交，可以没有
     */
    @RequiresPermissions(MENU_PREFIX + ADD)
    @PostMapping(ADD)
    @ResponseBody
    public ResultEntity add(String name, Integer parentId, @RequestParam(required = false) String parentName,
                            Integer orderNum, String url, @RequestParam(required = false) String icon, String enabled) {
        if (StringUtil.hasEmptyStrOrLessThanEqualsZeroNumber(name, orderNum, url, enabled)) {
            return ResultEntity.paramError(name, orderNum, url, enabled);
        }
        if (parentId == null || parentId < 0) {
            return ResultEntity.error("父菜单ID错误：" + parentId);
        }
        // 验证enabled字段的合法性
        Integer enable = MenuStatusEnum.codeOf(enabled);
        if (enable == null) {
            return ResultEntity.paramError("enabled字段不合法");
        }

        SysMenu parent;
        SysMenu sysMenu = new SysMenu();
        if (parentId != MenuTypeEnum.ZERO.getCode()) {
            parent = menuService.getById(parentId);
            // 父菜单类型不能是页面菜单
            if (parent == null || parent.getType() >= MenuTypeEnum.THIRD.getCode()) {
                return ResultEntity.paramError(parentId);
            }
            // 父菜单不匹配
            if (parentName != null && !parentName.equals(parent.getName())) {
                return ResultEntity.paramError(parentName);
            }
            sysMenu.setUrl(url);
            sysMenu.setType(parent.getType() + 1);
        } else {
            sysMenu.setUrl("#");
            sysMenu.setType(parentId + 1);
        }
        if (icon != null && icon.startsWith(ICON_START_FLAG)) {
            sysMenu.setIcon(icon);
        }
        sysMenu.setName(name);
        sysMenu.setParentId(parentId);
        sysMenu.setOrderNum(orderNum);
        sysMenu.setCreateTime(new Date());
        sysMenu.setEnabled(enable);

        boolean success = menuService.insertRoleMenu(this.getRoleId(), sysMenu);
        return returnResult(success, "添加菜单失败");
    }

    /**
     * 获取菜单树，选择父级菜单用(1，2级)
     */
    @RequiresPermissions(MENU_PREFIX + ADD)
    @PostMapping(TREE)
    @ResponseBody
    public List<ZTreeNode> tree() {
        List<SysMenu> list = menuService.listMenuTree(this.getRoleId());
        return ZTreeTool.toZTreeNodeList(list);
    }

    /**
     * 跳转到菜单详情列表页面
     */
    @RequiresPermissions(MENU_PREFIX + EDIT)
    @GetMapping(EDIT + "/{id}")
    @Override
    public String edit(@PathVariable Integer id, Model model) {
        SysMenu sysMenu = menuService.getById(id);
        if (sysMenu == null) {
            return ERROR;
        }
        String parentName = "-";
        if (sysMenu.getParentId() != MenuTypeEnum.ZERO.getCode()) {
            parentName = menuService.getName(sysMenu.getParentId());
        }
        model.addAttribute("menu", sysMenu);
        model.addAttribute("parentName", parentName);
        return TO_EDIT;
    }

    /**
     * 编辑菜单一定需要以下参数
     *
     * @param id       菜单id
     * @param name     菜单名称
     * @param parentId 父菜单id
     * @param parentName 父菜单名称
     * @param orderNum 菜单排序
     * @param url      url地址
     * @param icon     图标（如果是一级菜单，否则会自动忽略）
     * @param enabled  是否可用
     */
    @RequiresPermissions(MENU_PREFIX + EDIT)
    @PostMapping(EDIT)
    @ResponseBody
    public ResultEntity edit(Integer id, String name, Integer parentId, @RequestParam(required = false) String parentName,
                             Integer orderNum, String url, @RequestParam(required = false) String icon, String enabled) {
        if (StringUtil.hasEmptyStrOrLessThanEqualsZeroNumber(name, orderNum, url, enabled)) {
            return this.paramError(name, orderNum, url, enabled);
        }
        // 验证enabled字段的合法性
        Integer enable = MenuStatusEnum.codeOf(enabled);
        if (enable == null) {
            return ResultEntity.paramError("enabled字段不合法");
        }
        if (parentId != MenuTypeEnum.ZERO.getCode()) {
            SysMenu parent = menuService.getById(parentId);
            //父菜单类型不能是页面菜单
            if (parent == null || parent.getType() >= MenuTypeEnum.THIRD.getCode() || !parent.getName().equals(parentName)) {
                return ResultEntity.paramError();
            }
        }
        SysMenu sysMenu = new SysMenu();
        sysMenu.setId(id);
        sysMenu.setName(name);
        sysMenu.setParentId(parentId);
        sysMenu.setUrl(url);
        sysMenu.setOrderNum(orderNum);
        sysMenu.setIcon(icon);
        sysMenu.setEnabled(enable);

        //更新菜单，忽略为的null的更新值
        boolean success = menuService.updateByIdIgnoreNull(sysMenu);
        return returnResult(success, "修改菜单失败");
    }

    /**
     * 删除菜单
     */
    @RequiresPermissions(MENU_PREFIX + REMOVE)
    @PostMapping(REMOVE)
    @ResponseBody
    @Override
    public ResultEntity remove(Integer id) {
        if (id <= 0) {
            return ResultEntity.paramError();
        }
        boolean success = menuService.deleteMenu(id);
        return returnResult(success, "删除菜单失败");
    }
}
