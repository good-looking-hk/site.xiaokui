package site.xiaokui.config.beetl;


import com.zaxxer.hikari.HikariDataSource;
import org.beetl.core.resource.*;
import org.beetl.ext.spring.BeetlGroupUtilConfiguration;
import org.beetl.ext.spring.BeetlSpringViewResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

import static site.xiaokui.module.sys.blog.BlogConstants.BLOG_START_FLAG;
import static site.xiaokui.module.sys.blog.BlogConstants.UPLOAD_PATH;

/**
 * @author HK
 * @date 2018-05-22 22:39
 */
@Configuration(value = "beetlConfiguration")
public class BeetlConfig {

    @Bean(name = "datasource")
    public DataSource datasource(Environment env) {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(env.getProperty("spring.datasource.url"));
        ds.setUsername(env.getProperty("spring.datasource.username"));
        ds.setPassword(env.getProperty("spring.datasource.password"));
        ds.setDriverClassName(env.getProperty("spring.datasource.driver-class-name"));
        return ds;
    }

    @Bean(name = "beetlConfig")
    public BeetlGroupUtilConfiguration getBeetlGroupUtilConfiguration() {
        BeetlGroupUtilConfiguration beetlGroupUtilConfiguration = new BeetlConfiguration();
        // 获取Spring Boot 的ClassLoader
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = BeetlConfig.class.getClassLoader();
        }
        // Spring boot默认的模板路径
        ClasspathResourceLoader classpathResourceLoader = new ClasspathResourceLoader(classLoader,
                "templates");

        // 自定义的模板加载器
        FileResourceLoader fileResourceLoader = new FileResourceLoader(UPLOAD_PATH);

        CompositeResourceLoader compositeResourceLoader = new CompositeResourceLoader();
        compositeResourceLoader.addResourceLoader(new StartsWithMatcher(BLOG_START_FLAG), fileResourceLoader);
        compositeResourceLoader.addResourceLoader(new AllowAllMatcher(), classpathResourceLoader);

        beetlGroupUtilConfiguration.setResourceLoader(compositeResourceLoader);
        beetlGroupUtilConfiguration.init();
        // 如果使用了优化编译器，涉及到字节码操作，需要添加ClassLoader
        beetlGroupUtilConfiguration.getGroupTemplate().setClassLoader(classLoader);
        return beetlGroupUtilConfiguration;
    }

    @Bean(name = "beetlViewResolver")
    public BeetlSpringViewResolver getBeetlSpringViewResolver() {
        BeetlSpringViewResolver beetlSpringViewResolver = new BeetlSpringViewResolver();
        beetlSpringViewResolver.setContentType("text/html;charset=UTF-8");
        beetlSpringViewResolver.setSuffix(".html");
        beetlSpringViewResolver.setOrder(0);
        beetlSpringViewResolver.setConfig(getBeetlGroupUtilConfiguration());
        return beetlSpringViewResolver;
    }
}