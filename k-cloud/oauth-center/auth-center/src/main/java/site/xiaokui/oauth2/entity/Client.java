package site.xiaokui.oauth2.entity;

import lombok.Data;
import org.beetl.sql.core.annotatoin.Table;
import site.xiaokui.base.entity.BaseEntity;

import java.io.Serializable;

/**
 * @author HK
 */
@Table(name = "oauth2_client")
@Data
public class Client extends BaseEntity implements Serializable {

    private Long id;
    private String clientId;
    private String clientName;
    private String clientSecret;
}
