package site.xiaokui.oauth2.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author hk
 */
@Slf4j
@Configuration
public class ShiroConfig {

    /**
     * 配置shiro拦截器链
     * anon:所有url都都可以匿名访问
     * authc: 需要认证才能进行访问
     * user:配置记住我（通过cookie）或认证通过可以访问
     * 顺序从上到下,优先级依次降低
     * 当应用开启了rememberMe时,用户下次访问时可以是一个user,但不会是authc,因为authc是需要重新认证的
     * 1. /**的意思是所有文件夹及里面的子文件夹(包含文件夹里面的子文件)
     * 2. /*是所有文件夹，不含子文件夹
     * @param securityManager shiro的核心，默认使用{@link DefaultWebSecurityManager}
     * @return shiro过滤链（以FactoryBean）
     */
    @Bean
    public ShiroFilterFactoryBean shiroFilter(DefaultWebSecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilter = new ShiroFilterFactoryBean();
        shiroFilter.setSecurityManager(securityManager);
        // 登陆访问url,默认为/login.jsp(一般情况下建议主动修改)
        shiroFilter.setLoginUrl("/login");
        // 没有权限跳转的url
        shiroFilter.setUnauthorizedUrl("/error");

        Map<String, String> filterMap = new LinkedHashMap<>();
        filterMap.put("/*/**", "anon");
        shiroFilter.setFilterChainDefinitionMap(filterMap);
        return shiroFilter;
    }

    @Bean
    public DefaultWebSecurityManager securityManager(ShiroRealm shiroRealm) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(shiroRealm);
        return securityManager;
    }
}
