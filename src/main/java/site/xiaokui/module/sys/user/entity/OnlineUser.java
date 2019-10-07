package site.xiaokui.module.sys.user.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author HK
 * @date 2018-06-19 23:24
 */
@Data
public class OnlineUser {

    /**
     * sessionId
     */
    private String id;

    private String userId;

    private String username;

    private String ip;

    private String status;

    private Date startTimestamp;

    private Date lastAccessTime;

    private Long timeout;

    private String location;
}
