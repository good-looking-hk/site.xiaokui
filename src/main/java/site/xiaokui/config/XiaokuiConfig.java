package site.xiaokui.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * 小葵博客设置，暂时未使用到
 * // TODO
 * @author HK
 * @date 2018-06-30 11:43
 */
@Getter@Setter
@Component
@PropertySource("classpath:xiaokui.yml")
@ConfigurationProperties(prefix = XiaokuiConfig.PREFIX)
public class XiaokuiConfig {

    public static final String PREFIX = "xiaokui";

    /**
     * 小葵根目录，请确保有相应的访问权限，这是个绝对路径，默认是linux根目录xiaokui
     */
    private String baseUploadPath = "/xiaokui";

    /**
     * 博客上传目录
     */
    private String blogUploadPath = baseUploadPath + "/upload";

    /**
     * 用户头像上传保存路径
     */
    private String avatarUploadPath = "/xiaokui/img";
}
