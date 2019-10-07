package site.xiaokui.module.sys.music;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import site.xiaokui.module.base.BaseFileHelper;
import site.xiaokui.module.base.SpringContextHolder;
import site.xiaokui.module.sys.blog.util.BlogFileHelper;

import java.io.File;

/**
 * 重写FileUtil，使之更加简单规范而强大
 * @author HK
 * @date 2018-06-26 16:05
 */
@Slf4j
@Component
public class MusicFileHelper extends BaseFileHelper {

    public static MusicFileHelper getInstance() {
        return SpringContextHolder.getBean(MusicFileHelper.class);
    }

    @Value("${xiaokui.blogMusicPath}")
    @Override
    public void setBasePath(String blogMusicPath) {
        this.basePath = blogMusicPath;
        File file = new File(basePath);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                throw new RuntimeException("非法的上传路径：" + basePath + "！ 请检查！");
            }
        }
        log.info("xiaokui.blogMusicPath:" + basePath);
    }
}
