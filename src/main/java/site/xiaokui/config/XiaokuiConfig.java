package site.xiaokui.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author HK
 * @date 2018-06-30 11:43
 */
@Component
@ConfigurationProperties(prefix = XiaokuiConfig.PREFIX)
public class XiaokuiConfig {

    public static final String PREFIX = "xiaokui";

    private String baseUploadPath;

    private String blogUploadPath;

    private String avatarUploadPath;
}
