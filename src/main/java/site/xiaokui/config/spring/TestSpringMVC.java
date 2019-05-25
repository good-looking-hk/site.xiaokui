package site.xiaokui.config.spring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.io.IOException;

/**
 * Spring MVC测试类，便于理解过滤器、拦截器、监听器的运作过程，下面为结果
 * 1.Filter会伴随着Sping初始化，首先是调用ini方法，关闭Spring时会调用destroy方法,中间进行过滤方法
 * 2.关于Session会话会在连接建立的时候创建，然后才会进行请求的处理（如过滤、拦截），其生命周期伴依赖客户端连接及服务端设置
 * 3.拦截是依赖AOP思想的
 * 4.filter初始化-建立session会话-执行过滤器-放行请求-执行请求-完成请求返回结果-释放连接资源
 * 5.如果想使用servelt标准注解，即@Servlet、@WebFilter和@WebListener，那么需要在主类上添加@ServletComponentScan
 * 6.使用@Configuration以启用测试
 *
 * @author HK
 * @date 2019-02-25 13:41
 */
@Slf4j
public class TestSpringMVC implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/test1/test").setViewName("error");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new TestInterceptor()).addPathPatterns("/test1/*/**");
        WebMvcConfigurer.super.addInterceptors(registry);
    }

    @Bean
    public FilterRegistrationBean filterRegist() {
        FilterRegistrationBean<Filter> frBean = new FilterRegistrationBean<>();
        frBean.setFilter(new TestFilter());
        frBean.addUrlPatterns("/test1/*");
        return frBean;
    }

    @Bean
    public ServletListenerRegistrationBean listenerRegist() {
        ServletListenerRegistrationBean<HttpSessionListener> srb = new ServletListenerRegistrationBean<>();
        srb.setListener(new TestListener());
        return srb;
    }

    private static class TestFilter implements Filter {
        @Override
        public void init(FilterConfig filterConfig) throws ServletException {
            log.debug(filterConfig.getFilterName() + "初始化");
        }

        /**
         * 不做任何事则chain.doFilter(request, response)即可
         */
        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
            log.debug("执行过滤器");
            sleep(100);
            chain.doFilter(request, response);
        }

        @Override
        public void destroy() {
            log.debug("注销过滤器");
        }
    }

    private static class TestInterceptor implements HandlerInterceptor {
        /**
         * 如果请求放过（调用Controller之前），则返回true，否则返回false
         */
        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
            log.debug("放行请求：" + request.getRequestURI());
            sleep(100);
            return true;
        }

        /**
         * 请求已被处理完（调用Controller之后，返回逻辑视图之前），调用此方法
         */
        @Override
        public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
            log.debug("执行请求完，返回" + response.getStatus());
            sleep(100);
        }

        /**
         * 在返回逻辑试图之后（通常用来释放资源），调用此方法
         */
        @Override
        public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
            log.debug("关闭连接资源");
            sleep(100);
        }
    }

    private static class TestListener implements HttpSessionListener {
        @Override
        public void sessionCreated(HttpSessionEvent se) {
            log.debug("创建session" + se.getSession());
            sleep(100);
        }

        @Override
        public void sessionDestroyed(HttpSessionEvent se) {
            log.debug("注销session" + se.getSession());
            sleep(100);
        }
    }

    private static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
        }
    }

    private TestSpringMVC() {
        if (!log.isDebugEnabled()) {
            throw new RuntimeException("这是测试配置哟，请去掉@Configuration、@Component注解");
        }
        log.debug("测试Spring MVC配置({})", this.getClass().getCanonicalName());
    }
}
