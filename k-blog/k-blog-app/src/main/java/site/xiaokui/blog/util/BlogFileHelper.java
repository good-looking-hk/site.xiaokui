package site.xiaokui.blog.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import site.xiaokui.base.service.BaseFileHelper;
import site.xiaokui.base.service.SpringContextHolder;

import java.io.File;

/**
 * 重写FileUtil，使之更加简单规范而强大
 * @author HK
 * @date 2018-06-26 16:05
 */
@Slf4j
@Component
public class BlogFileHelper extends BaseFileHelper {

    public static BlogFileHelper getInstance() {
        return SpringContextHolder.getBean(BlogFileHelper.class);
    }

    /**
     * Spring初始化bean时会自动注入并调用该方法
     */
    @Value("${xiaokui.blogUploadPath}")
    @Override
    protected void setBasePath(String blogUploadPath) {
        this.basePath = blogUploadPath;
        File file = new File(basePath);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                throw new RuntimeException("非法的上传路径：" + basePath + "！ 请检查！");
            }
        }
        log.info("xiaokui.blogUploadPath:" + basePath);
    }

    public static String  getTempDir() {
        return BlogFileHelper.TEMP_DIR;
    }
}
