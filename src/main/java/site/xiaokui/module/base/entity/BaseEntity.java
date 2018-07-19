package site.xiaokui.module.base.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import site.xiaokui.module.sys.user.entity.ToZTreeNode;
import site.xiaokui.module.sys.user.entity.ZTreeNode;

import java.io.Serializable;
import java.util.Date;

/**
 * @author HK
 * @date 2018-06-10 15:50
 */
@ToString
@Getter
@Setter
public class BaseEntity<T extends BaseEntity> implements ToZTreeNode, Serializable, Comparable<T> {

    protected Integer id;

    protected String name;

    protected Integer parentId;

    protected Integer orderNum;

    protected Date createTime;

    protected Date modifiedTime;

    @Override
    public int compareTo(T t) {
        if (!this.parentId.equals(t.getParentId())) {
            return this.parentId.compareTo(t.getParentId());
        }
        if (!this.orderNum.equals(t.getOrderNum())) {
            return this.orderNum.compareTo(t.getOrderNum());
        }
        return this.id.compareTo(t.getId());
    }

    @Override
    public ZTreeNode toZTreeNode() {
        return new ZTreeNode(this.id, this.parentId, this.name);
    }
}
