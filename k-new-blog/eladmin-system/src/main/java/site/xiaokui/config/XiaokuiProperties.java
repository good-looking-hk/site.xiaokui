package site.xiaokui.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author HK
 * @date 2020-12-02 16:07
 */
@Data
@Component
@ConfigurationProperties(prefix = "xiaokui")
public class XiaokuiProperties {

    private int mostView;

    private int recentUpload;

    private int recentUpdate;

    private int recommendCount;

    private int singleIpContributeBlog;

    private int singleIpContributeAllBlog;

    private int singleUserContributeBlog;

    private int singleUserContributeAllBlog;

    private String basePath;

    private String filePath;

    private String staticLibPath;

    private String blogUploadPath;

    private String blogMusicPath;

    private String avatarUploadPath;

    private String logsPath;

    private String logName;
}
