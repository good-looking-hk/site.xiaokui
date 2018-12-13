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
public class BaseEntity implements ToZTreeNode, Serializable {

    protected Integer id;

    protected String name;

    /**
     * 创建时间在数据库中一般用datetime类型
     */
    protected Date createTime;

    /**
     * 修改时间在数据库中一般用timestamp类型，由数据库自动修改
     * 但个人而言，还是习惯用datetime，自己完全控制在代码层，不依赖于数据库
     */
    protected Date modifiedTime;

    @Override
    public ZTreeNode toZTreeNode() {
        return new ZTreeNode(this.id, null, this.name);
    }
}
