package site.xiaokui.blog.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author HK
 * @date 2019-06-10 16:15
 */
@Configuration
public class SpringWebConfig implements WebMvcConfigurer {

    @Value("${spring.profiles.active}")
    private String profile;

    @Value("${xiaokui.staticLibPath}")
    private String staticLibPath;

    @Value("${xiaokui.blogMusicPath}")
    private String blogMusicPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        if (this.staticLibPath == null) {
            throw new IllegalArgumentException("xiaokui.staticLibPath参数未设置:" + null);
        }
        registry.addResourceHandler("/lib/**").addResourceLocations("file:" + staticLibPath);
        registry.addResourceHandler("/music/**").addResourceLocations("file:" + blogMusicPath);
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        for (HttpMessageConverter converter : converters) {
            if (converter instanceof StringHttpMessageConverter) {
                StringHttpMessageConverter messageConverter = (StringHttpMessageConverter) converter;
                messageConverter.setDefaultCharset(StandardCharsets.UTF_8);
            }
        }
    }
}
