package site.xiaokui.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * 小葵博客设置，暂时未使用到
 * 可用@PropertySource("classpath:xiaokui.yml")制定配置文件
 * @author HK
 * @date 2018-06-30 11:43
 */
@Getter@Setter
@Component
@ConfigurationProperties(prefix = "xiaokui")
public class XiaokuiConfig {

    public String basePath;

    /**
     * 小葵根目录，请确保有相应的访问权限，这是个绝对路径，默认是linux根目录/xiaokui
     */
    private String staticLibsPath;

    /**
     * 博客上传目录
     */
    private String blogUploadPath;

    /**
     * 音乐上传目录
     */
    private String blogMusicPath;

    /**
     * 用户头像上传保存路径
     */
    private String avatarUploadPath;
}
