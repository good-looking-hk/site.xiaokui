package site.xiaokui.config;


import org.beetl.core.resource.*;
import org.beetl.ext.spring.BeetlGroupUtilConfiguration;
import org.beetl.ext.spring.BeetlSpringViewResolver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;


/**
 * @author HK
 * @date 2018-05-22 22:39
 */
@Primary
@Configuration(value = "beetlConfiguration")
public class BeetlConfig {

    public static final String BLOG_START_FLAG = "blog:";

    public static final String FILE_START_FLAG = "file:";

    @Bean(name = "beetlConfig")
    public BeetlGroupUtilConfiguration getBeetlGroupUtilConfiguration(XiaokuiProperties xiaokuiProperties) {
        BeetlGroupUtilConfiguration beetlGroupUtilConfiguration = new BeetlGroupUtilConfiguration();
        // 获取Spring Boot 的ClassLoader
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = BeetlConfig.class.getClassLoader();
        }
        // Spring boot默认的模板路径
        ClasspathResourceLoader classpathResourceLoader = new ClasspathResourceLoader(classLoader, "templates");

        // 自定义的模板加载器，linux环境下默认为/xiaokui/upload/
        FileResourceLoader blogFileResourceLoader = new FileResourceLoader(xiaokuiProperties.getBlogUploadPath());
        FileResourceLoader fileResourceLoader = new FileResourceLoader("");

        CompositeResourceLoader compositeResourceLoader = new CompositeResourceLoader();
        compositeResourceLoader.addResourceLoader(new StartsWithMatcher(BLOG_START_FLAG), blogFileResourceLoader);
        compositeResourceLoader.addResourceLoader(new StartsWithMatcher(FILE_START_FLAG), fileResourceLoader);
        compositeResourceLoader.addResourceLoader(new AllowAllMatcher(), classpathResourceLoader);

        beetlGroupUtilConfiguration.setResourceLoader(compositeResourceLoader);
        beetlGroupUtilConfiguration.init();
        // 如果使用了优化编译器，涉及到字节码操作，需要添加ClassLoader
        beetlGroupUtilConfiguration.getGroupTemplate().setClassLoader(classLoader);
        return beetlGroupUtilConfiguration;
    }

    @Bean(name = "beetlViewResolver")
    public BeetlSpringViewResolver getBeetlSpringViewResolver(BeetlGroupUtilConfiguration bc) {
        BeetlSpringViewResolver beetlSpringViewResolver = new BeetlSpringViewResolver();
        beetlSpringViewResolver.setContentType("text/html;charset=UTF-8");
        beetlSpringViewResolver.setSuffix(".html");
        beetlSpringViewResolver.setOrder(0);
        beetlSpringViewResolver.setConfig(bc);
        return beetlSpringViewResolver;
    }
}