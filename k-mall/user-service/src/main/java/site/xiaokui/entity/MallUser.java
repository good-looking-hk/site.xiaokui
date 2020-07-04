package site.xiaokui.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @author HK
 * @date 2020-07-04 11:13
 */
@Entity
public class MallUser {

    @Id
    private Long uid;

    private String username;

    private String password;

    private String status;

    private String remark;

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
