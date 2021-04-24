package site.xiaokui.weixin.mp.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;
import site.xiaokui.base.entity.BaseEntity;

/**
 * @author HK
 * @date 2020-10-02 16:42
 */
@EqualsAndHashCode(callSuper=false)
@Data
public class MsgLog extends BaseEntity implements Serializable {

    /**
     * 自增ID
     */
    private Long id;

    /**
     * 是否为接受消息
     */
    private Boolean isReceive;

    /**
     * 发生日期
     */
    private Integer occurDate;

    /**
     * 发生时间
     */
    private Date occurTime;

    /**
     * 发送者
     */
    private String fromUser;

    /**
     * 接受者
     */
    private String toUser;

    /**
     * 消息内容
     */
    private String msgContent;

    /**
     * 消息类型
     */
    private String msgType;

    /**
     * 消息ID
     */
    private Long msgId;

    /**
     * 备注
     */
    private String remark;
}