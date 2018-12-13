package site.xiaokui.module.base.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import site.xiaokui.module.sys.user.entity.ZTreeNode;

/**
 * 用于展现具有父子结构的实体关系
 * @author HK
 * @date 2018-10-03 17:13
 */
@ToString
@Getter
@Setter
public class ParentEntity extends BaseEntity implements Comparable<ParentEntity> {

    protected Integer parentId;

    protected Integer orderNum;

    @Override
    public int compareTo(ParentEntity t) {
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
