package site.xiaokui.base.config;

import com.zaxxer.hikari.HikariDataSource;
import org.beetl.core.resource.*;
import org.beetl.ext.spring.BeetlGroupUtilConfiguration;
import org.beetl.ext.spring.BeetlSpringViewResolver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

/**
 * 给beetl、beetlsql添加默认配置
 * @author HK
 * @date 2020-11-13 11:15
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
        BeetlGroupUtilConfiguration beetlGroupUtilConfiguration = new BeetlGroupUtilConfiguration();
        // 获取Spring Boot 的ClassLoader
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = BeetlConfig.class.getClassLoader();
        }
        // Spring boot默认的模板路径
        ClasspathResourceLoader classpathResourceLoader = new ClasspathResourceLoader(classLoader, "templates");

        CompositeResourceLoader compositeResourceLoader = new CompositeResourceLoader();
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
