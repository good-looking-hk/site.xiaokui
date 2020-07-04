package site.xiaokui.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author HK
 * @date 2020-07-02 14:36
 */
@Entity
public class MallProduct {

    @Id
    private Long pid;

    private String name;

    private BigDecimal price;

    private Long stock;

    private Date createTime;

    private String remark;

    public Long getPid() {
        return pid;
    }

    public void setPid(Long pid) {
        this.pid = pid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Long getStock() {
        return stock;
    }

    public void setStock(Long stock) {
        this.stock = stock;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "MallProduct{" +
                "pid=" + pid +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", stock=" + stock +
                ", createTime=" + createTime +
                ", remark='" + remark + '\'' +
                '}';
    }
}
