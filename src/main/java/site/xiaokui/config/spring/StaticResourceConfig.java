package site.xiaokui.config.spring;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import site.xiaokui.module.base.BaseConstants;

/**
 * 留作纪念，不删了
 *
 * @author HK
 * @date 2019-06-10 16:15
 */
@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {

    @Value("${spring.profiles.active}")
    private String profile;

    @Value("${xiaokui.staticLibsPath}")
    private String staticLibsPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        if (this.staticLibsPath == null) {
            throw new IllegalArgumentException("xiaokui.staticLibsPath参数未设置:" + null);
        }
        registry.addResourceHandler("/lib/**").addResourceLocations("file:" + staticLibsPath);
    }
}
