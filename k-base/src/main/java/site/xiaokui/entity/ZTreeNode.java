package site.xiaokui.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 对ZTree节点的封装，详情见http://www.treejs.cn/
 *
 * @author HK
 * @date 2018-05-29 22:38
 */
@Data
@AllArgsConstructor
public class ZTreeNode {

    /**
     * 节点id
     */
    protected Integer id;

    /**
     * 父节点id
     */
    protected Integer parentId;

    /**
     * 节点名称
     */
    protected String name;

    /**
     * 该节点是否处于打开状态，仅对父节点有效
     */
    protected Boolean open;

    /**
     * 是否被选中，前面有钩钩
     */
    private Boolean checked;

    public ZTreeNode(Integer id, Integer parentId, String name) {
        this(id, parentId, name, null, null);
        if (parentId == 0) {
            this.setOpen(true);
        }
    }

    public static ZTreeNode createRoot() {
        return new ZTreeNode(0, 0, "顶级");
    }
}
