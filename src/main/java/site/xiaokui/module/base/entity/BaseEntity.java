package site.xiaokui.module.base.entity;

import cn.hutool.core.date.DateUtil;
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
@ToString@Getter@Setter
public class BaseEntity implements ToZTreeNode, Serializable {

    protected Integer id;

    protected String name;

    /**
     * 创建时间在数据库中一般用datetime类型
     */
    protected Date createTime;

    /**
     * 关于修改时间的确定，可以自己在代码层控制，也可以依赖于数据库的时间戳自动更新
     * 两者都行，建议是依赖于数据库自动更新（建表时需要设置）
     * 这个是由数据库自动更新的
     */
    protected Date modifiedTime;

    /**
     * String拼接的底层实现也是依赖StringBuilder
     */
    public String toSimpleString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getSimpleName()).append('[').append("id=").append(id).append(",name=");
        sb.append(name).append(",createTime=").append(DateUtil.formatDate(createTime) + "]");
        return sb.toString();
    }

    @Override
    public ZTreeNode toZTreeNode() {
        return new ZTreeNode(this.id, null, this.name);
    }
}
