package me.zhengjie.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import site.xiaokui.config.RequestInterceptor;
import site.xiaokui.config.XiaokuiProperties;

import java.io.IOException;

/**
 * Spring Web环境相关配置
 * @author HK
 * @date 2020-04-26 11:06
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * 加载配置文件
     */
    private final XiaokuiProperties xiaokuiProperties;

    public WebConfig(XiaokuiProperties xiaokuiProperties, ObjectMapper objectMapper) {
        this.xiaokuiProperties = xiaokuiProperties;
        // 将null转为空字符串
        objectMapper.getSerializerProvider().setNullValueSerializer(new JsonSerializer<Object>() {
            @Override
            public void serialize(Object paramT, JsonGenerator paramJsonGenerator,
                                  SerializerProvider paramSerializerProvider) throws IOException {
                paramJsonGenerator.writeString("");
            }
        });
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    /**
     * 配置静态资源访问地址
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/avatar/**").addResourceLocations("file:" + xiaokuiProperties.getAvatarUploadPath());
        registry.addResourceHandler("/file/**").addResourceLocations("file:" + xiaokuiProperties.getFilePath());
        registry.addResourceHandler("/lib/**").addResourceLocations("file:" + xiaokuiProperties.getStaticLibPath());
        registry.addResourceHandler("/music/**").addResourceLocations("file:" + xiaokuiProperties.getBlogMusicPath());
        registry.addResourceHandler("/**").addResourceLocations("classpath:/static/", "classpath:/META-INF/resources/");
    }

    /**
     * 配置拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new RequestInterceptor());
    }
}
