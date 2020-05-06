package site.xiaokui.module.user.util;

import site.xiaokui.entity.BaseEntity;
import site.xiaokui.entity.ZTreeNode;
import site.xiaokui.module.user.entity.SysMenu;

import java.util.LinkedList;
import java.util.List;

/**
 * @author HK
 * @date 2018-06-10 19:13
 */
public class ZTreeTool {

    /**
     * 父类的静态方法可以被子类继承，但是不能重写(留作纪念)
     */
    public static List<ZTreeNode> toZTreeNodeList(List<? extends BaseEntity> list) {
        List<ZTreeNode> nodes = new LinkedList<>();
        ZTreeNode root = ZTreeNode.createRoot();
        nodes.add(root);
        for (BaseEntity t : list) {
            nodes.add(t.toZTreeNode());
        }
        return nodes;
    }

    /**
     * 根据角色菜单和所有菜单，合并两项，为所有菜单中的角色菜单项添加checked:true标记
     */
    public static List<ZTreeNode> findRoleMenuTree(List<SysMenu> roleMenus, List<SysMenu> allMenus) {
        List<ZTreeNode> roleNodes = toZTreeNodeList(roleMenus);
        List<ZTreeNode> allNodes = toZTreeNodeList(allMenus);
        for (ZTreeNode node : allNodes) {
            // ZTreeNode重写了equals方法
            if (roleNodes.contains(node)) {
                node.setChecked(true);
            }
        }
        return allNodes;
    }
}
