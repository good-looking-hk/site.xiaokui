package site.xiaokui.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author HK
 * @date 2020-07-04 20:07
 */
@Entity
public class MallOrder {

    /**
     * 订单编号
     * 处理前端Long型的精度丢失，修改jackson序列化方式
     */
    @Id
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long oid;

    /**
     * 用户编号
     */
    private Long uid;

    /**
     * 商品编号
     */
    private Long pid;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 商品价格
     */
    private BigDecimal price;

    /**
     * 订单状态
     */
    private Integer status;

    /**
     * 支付信息
     */
    private String payMsg;

    /**
     * 订单备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 失效时间
     */
    private Date expireTime;

    /**
     * 完成时间
     */
    private Date completeTime;

    public Long getOid() {
        return oid;
    }

    public void setOid(Long oid) {
        this.oid = oid;
    }

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getPayMsg() {
        return payMsg;
    }

    public void setPayMsg(String payMsg) {
        this.payMsg = payMsg;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }

    public Date getCompleteTime() {
        return completeTime;
    }

    public void setCompleteTime(Date completeTime) {
        this.completeTime = completeTime;
    }

    @Override
    public String toString() {
        return "MallOrder{" +
                "oid=" + oid +
                ", uid=" + uid +
                ", pid=" + pid +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", status=" + status +
                ", payMsg='" + payMsg + '\'' +
                ", remark='" + remark + '\'' +
                ", createTime=" + createTime +
                ", expireTime=" + expireTime +
                ", completeTime=" + completeTime +
                '}';
    }
}
