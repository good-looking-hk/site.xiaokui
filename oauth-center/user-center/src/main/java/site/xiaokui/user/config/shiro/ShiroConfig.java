package site.xiaokui.user.config.shiro;

import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.SessionListener;
import org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO;
import org.apache.shiro.session.mgt.eis.MemorySessionDAO;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.session.mgt.WebSessionManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.MethodInvokingFactoryBean;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import site.xiaokui.common.util.StringUtil;
import site.xiaokui.base.constant.BaseConstants;

import javax.servlet.Filter;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import static org.apache.shiro.session.mgt.AbstractSessionManager.DEFAULT_GLOBAL_SESSION_TIMEOUT;
import static org.apache.shiro.session.mgt.AbstractValidatingSessionManager.DEFAULT_SESSION_VALIDATION_INTERVAL;
import static site.xiaokui.user.config.shiro.ShiroConstants.SHIRO_ACTIVE_SESSION_CACHE_NAME;

/**
 * 配置shiro
 * <p>
 * 一：对于shiro的使用，由于她所提供的API非常精简，以至于我们都觉得她太简单了，但其实不是，shiro对于
 * 复杂的配置都进行的简化及默认实现（即你如果没配置，那么就是她的默认实现），这种方式对于小白是友好的，但
 * 是对于需要深度定制的人来说，还是得自己去研究她的内部构造，
 * </p>
 * <p>
 * 二：这里说明两个概念，一个是关于session的概念，我们知道，
 * 1.session的有效期是会话期间，而session是依托cookie的，这里cookie的有效期是被设置为直至会话结束
 * 2.rememberMe也是个cookie，只不过它的有效期值是由服务端设定的（一般来说比较长，浏览器可以修改）
 * </p>
 * <p><
 * 三：简单的配置，那么只需要配置一个Reaml、一个shiroFilter以及一个securityManager就可以满足简单的日常开发了
 * 1.大体上shiro的流程是Application Code->Subject->Security Manager->Realm,这个有过一定经验就能体会到这一点
 * 2.SecurityManager是shiro的心脏，这也是为什么SecurityManager是必须注入的。DefaultWebSecurityManager是
 * web开发的必须选择
 * 3.Realm是可以有多个，且shiro提供的多种Realm实现，如使用sql查询的{@link org.apache.shiro.realm.jdbc.JdbcRealm}，
 * 使用ini文件的{@link org.apache.shiro.realm.text.IniRealm},可以指定Realm验证策略的
 * {@link org.apache.shiro.authc.pam.ModularRealmAuthenticator},具体策略请查看源码。一般情况下直接继承
 * {@link org.apache.shiro.realm.AuthorizingRealm}即可满足日常需求
 * 4.对于默认配置的一些列举：默认使用{@link DefaultWebSecurityManager}的情况下，session管理器为{@link DefaultWebSessionManager}、
 * 缓存管理器为{@code null}、cookie管理器为{@link CookieRememberMeManager}、sessionDAO为{@link MemorySessionDAO}、session类型为
 * {@link org.apache.shiro.session.mgt.SimpleSession}
 * 5.对于ehcache缓存的使用，大致有两个地方可以用到，一个是对于realm的缓存，一个是对于session的缓存。而如果使用默认配置的话，那么对于realm缓存
 * 是需要显式在realm中进行缓存判断的，否则即使配置了也不会生效；而对于session的缓存，shiro默认使用的是MemorySessionDAO，即将session信息保存
 * 在{@link java.util.concurrent.ConcurrentMap}中，这里为了迎合使用ehcache，我们替换掉
 * </p>
 * @author HK
 * @date 2018-05-22 19:03
 */
// @Configuration
public class ShiroConfig {

    private static final String ANYBODY = ShiroConstants.SHIRO_ANYBODY, REMEMBER_ME = ShiroConstants.SHIRO_REMEMBER_ME, LOGIN_USER = ShiroConstants.SHIRO_LOGIN_USER;

    /**
     * 项目自定义的Realm，必须作为bean注入到Spring
     * 这里记录一个坑，这个方法加上了@Bean注解，因此在本类其他Bean中可以通过注入的方法来获取，也可以直接调用方法来返回，两者等价
     * 即ShiroDbRealm realm = this.getShiroDbRealm()等价如注入的ShiroDbRealm Bean(Spring进行了代理)
     */
    @Bean
    public ShiroDbRealm getShiroDbRealm() {
        ShiroDbRealm shiroDbRealm = new ShiroDbRealm();
        // 是否对realm处理的结果进行缓存，这样可以避免重复执行realm中的权限验证
        // 由于已经在realm中实现了权限的缓存，所以这一步没必要
        // 简单的缓存实现可以使用MemoryConstrainedCacheManager
        shiroDbRealm.setCachingEnabled(false);
        return shiroDbRealm;
    }

    /**
     * shiro的核心,安全管理器，需要依赖注入的缓存管理器(目前使用的是ehcache，可以改为redis注入)
     */
    @Bean
    public DefaultWebSecurityManager securityManager(WebSessionManager sessionManager) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(this.getShiroDbRealm());
        // 针对session做缓存，这里可以为扩展为分布式session
        securityManager.setSessionManager(sessionManager);
        securityManager.setRememberMeManager(this.getRememberMeManager(this.getSimpleCookie()));
        return securityManager;
    }

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
     * @param onlineSessionFilter 在线过滤器
     * @return shiro过滤链（以FactoryBean）
     */
    @Bean
    public ShiroFilterFactoryBean shiroFilter(DefaultWebSecurityManager securityManager, OnlineSessionFilter onlineSessionFilter,
                                                    @Value("${spring.profiles.active}") String profile,
                                                    @Value("${xiaokui.enable-shiro}") boolean enableShiro) {
        ShiroFilterFactoryBean shiroFilter = new ShiroFilterFactoryBean();
        shiroFilter.setSecurityManager(securityManager);
        // 登陆访问url,默认为/login.jsp(一般情况下建议主动修改)
        shiroFilter.setLoginUrl("/login");
        // 没有权限跳转的url
        shiroFilter.setUnauthorizedUrl("/global/error");

        Map<String, Filter> map = new LinkedHashMap<String, Filter>();
        // 限制同一帐号同时在线的个数
        map.put(onlineSessionFilter.getFilterName(), onlineSessionFilter);
        shiroFilter.setFilters(map);

        Map<String, String> filterMap = new LinkedHashMap<>();

        // 静态资源的过滤，采用nginx?
        filterMap.put("/lib/**", ANYBODY);
        filterMap.put("/plugins/**", ANYBODY);
        filterMap.put("/font-awesome/**", ANYBODY);
        filterMap.put("/bootstrap/**", ANYBODY);
        filterMap.put("/layui/**", ANYBODY);
        filterMap.put("/**/font/**", ANYBODY);
        filterMap.put("/img/**", ANYBODY);
        filterMap.put("/music/**", ANYBODY);
        filterMap.put("/**/*.js", ANYBODY);
        filterMap.put("/**/*.css", ANYBODY);
        filterMap.put("/**/*.map", ANYBODY);
        filterMap.put("/**/*.png", ANYBODY);
        filterMap.put("/**/*.json", ANYBODY);
        filterMap.put("/unauthorized", ANYBODY);
        filterMap.put("/error", ANYBODY);
        filterMap.put("/favicon.ico", ANYBODY);

        // 特定请求的过滤
        filterMap.put("/", ANYBODY);
        filterMap.put("/index", ANYBODY);
        filterMap.put("/login", ANYBODY);
        filterMap.put("/register", ANYBODY);
        filterMap.put("/sys/login", ANYBODY);
        filterMap.put("/sys/register", ANYBODY);
        filterMap.put("/sys/music/1/july", ANYBODY);
        filterMap.put("/test", ANYBODY);

        // 放行博客访问链接
        filterMap.put("/blog/**", ANYBODY);
        filterMap.put("/wx/**", ANYBODY);

        // 其余都需要验证
        filterMap.put("/user", REMEMBER_ME);
        filterMap.put("/swagger/**", REMEMBER_ME);

        if (!BaseConstants.PROFILE_REMOTE.equals(profile)) {
            // 本地免登录
            filterMap.put("/**", StringUtil.addDot(onlineSessionFilter.getFilterName(), REMEMBER_ME));
        } else {
            // 线上需要登录
            filterMap.put("/**", StringUtil.addDot(onlineSessionFilter.getFilterName(), LOGIN_USER));
        }
        if (!enableShiro) {
            filterMap.clear();
            filterMap.put("/**/*", ANYBODY);
        }
        shiroFilter.setFilterChainDefinitionMap(filterMap);
        return shiroFilter;
    }

    @Bean
    public OnlineSessionFilter onlineSessionFilter(SessionDAO cacheSessionDAO) {
        return new OnlineSessionFilter(cacheSessionDAO);
    }

    /**
     * DefaultWebSessionManager
     * 注意，{@code DefaultWebSessionManager}设置的缓存<b>一定</b>会覆盖{@code SessionDAO}设置的缓存
     * 具体请看{@link org.apache.shiro.session.mgt.DefaultSessionManager#applyCacheManagerToSessionDAO}和
     * {@link org.apache.shiro.session.mgt.DefaultSessionManager#setSessionDAO}
     */
    @Bean
    public DefaultWebSessionManager getDefaultWebSessionManager(SessionDAO sessionDAO) {
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        LinkedList<SessionListener> list = new LinkedList<>();
        // 添加监听
        list.add(new ShiroSessionListener(sessionDAO));
        sessionManager.setSessionListeners(list);
        // 设置sessionDAO，不需要在此处设置缓存,如setCacheManager(xxx);会覆盖sessionDAO的缓存
        sessionManager.setSessionDAO(sessionDAO);
        // session失效时间，单位秒,shiro默认60分钟
        sessionManager.setSessionValidationInterval(DEFAULT_SESSION_VALIDATION_INTERVAL);
        // 多久检测一次失效的session,shiro默认30分钟
        sessionManager.setGlobalSessionTimeout(DEFAULT_GLOBAL_SESSION_TIMEOUT);
        // 网上找的，处理JSESSIONID
        sessionManager.setSessionIdUrlRewritingEnabled(false);
        // 删除失效session
        sessionManager.setDeleteInvalidSessions(true);
        sessionManager.setSessionValidationSchedulerEnabled(true);
        return sessionManager;
    }

    /**
     * 默认MemorySessionDAO：Simple memory-based implementation of the SessionDAO that stores all of its sessions in an in-memory
     * EnterpriseCacheSessionDAO：SessionDAO implementation that relies on an enterprise caching product as the EIS system of
     * record for all sessions
     * 一般情况下，默认的基于内存的MemorySessionDao已经可以满足需求，而EnterpriseCacheSessionDAO，顾名思义，是依靠缓存实现的
     * 需要注意的是，shiro默认session cache的名称就是shiro-activeSessionCache，这里需要ehcache配置文件保持一致
     * {@link EnterpriseCacheSessionDAO#ACTIVE_SESSION_CACHE_NAME}
     */
    @Bean
    public SessionDAO getEnterpriseCacheSessionDAO(CacheManager cacheManager) {
        EnterpriseCacheSessionDAO enterCacheSessionDAO = new EnterpriseCacheSessionDAO();
        // 添加缓存管理器
        enterCacheSessionDAO.setCacheManager(cacheManager);
        enterCacheSessionDAO.setActiveSessionsCacheName(SHIRO_ACTIVE_SESSION_CACHE_NAME);
        return enterCacheSessionDAO;
    }

    /**
     * 缓存管理器，默认使用Ehcache实现，由Spring自动注入进SessionDAO
     */
    @Bean
    public CacheManager getCacheShiroManager(EhCacheManagerFactoryBean ehcache) {
        EhCacheManager ehCacheManager = new EhCacheManager();
        ehCacheManager.setCacheManager(ehcache.getObject());
        return ehCacheManager;
    }

    /**
     * cookie设置
     */
    @Bean
    public SimpleCookie getSimpleCookie() {
        SimpleCookie simpleCookie = new SimpleCookie("rememberMe");
        simpleCookie.setHttpOnly(true);
        // 7天
        simpleCookie.setMaxAge(7 * 24 * 60 * 60);
        return simpleCookie;
    }

    /**
     * rememberMe管理器
     */
    @Bean
    public CookieRememberMeManager getRememberMeManager(SimpleCookie simpleCookie) {
        CookieRememberMeManager manager = new CookieRememberMeManager();
        //rememberMe cookie加密的密钥 建议每个项目都不一样 默认AES算法 密钥长度（128 256 512 位）
        manager.setCipherKey(Base64.decode("Z3VucwAAAAAAAAAAAAAAAA=="));
        manager.setCookie(simpleCookie);
        return manager;
    }

    /**
     * 在方法中 注入 securityManager,进行代理控制
     */
    @Bean
    public MethodInvokingFactoryBean methodInvokingFactoryBean(DefaultWebSecurityManager securityManager) {
        MethodInvokingFactoryBean bean = new MethodInvokingFactoryBean();
        bean.setStaticMethod("org.apache.shiro.SecurityUtils.setSecurityManager");
        bean.setArguments(new Object[]{securityManager});
        return bean;
    }

    /**
     * Shiro生命周期处理器
     * 用于在实现了Initializable接口的Shiro bean初始化时调用Initializable接口回调(例如:UserRealm)
     * 在实现了Destroyable接口的Shiro bean销毁时调用 Destroyable接口回调(例如:DefaultSecurityManager)
     */
    @Bean
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    /**
     * 开启shiro权限注解bean-1(如@RequiresRoles,@RequiresPermissions)，需两个bean
     * 没有此bean，@RequestMapping可能无法正常工作
     */
    @Bean
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator proxyCreator = new DefaultAdvisorAutoProxyCreator();
        proxyCreator.setProxyTargetClass(true);
        return proxyCreator;
    }

    /**
     * 启用shrio授权注解拦截方式，AOP式方法级权限检查bean-2
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(securityManager);
        return advisor;
    }
}
