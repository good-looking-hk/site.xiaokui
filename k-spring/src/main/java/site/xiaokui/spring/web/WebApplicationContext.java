package site.xiaokui.spring.web;

import site.xiaokui.spring.bean.DefaultWebBeanFactory;
import site.xiaokui.spring.web.server.HttpRequest;
import site.xiaokui.spring.web.server.HttpResponse;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * 1.Filter会伴随着Sping初始化，首先是调用ini方法，关闭Spring时会调用destroy方法,中间进行过滤方法
 * 2.关于Session会话会在连接建立的时候创建，然后才会进行请求的处理（如过滤、拦截），其生命周期伴依赖客户端连接及服务端设置
 * 3.拦截是依赖AOP思想的
 * 4.filter初始化-建立session会话-执行过滤器-放行请求-执行请求-完成请求返回结果-释放连接资源
 *
 * 1、Filter是依赖于Servlet容器，属于Servlet规范的一部分，而拦截器则是独立存在的，可以在任何情况下使用。
 * 2、Filter的执行由Servlet容器回调完成，而拦截器通常通过动态代理的方式来执行。
 * 3、Filter的生命周期由Servlet容器管理，而拦截器则可以通过IoC容器来管理，因此可以通过注入等方式来获取其他Bean的实例，因此使用会更方便。
 * @author HK
 * @date 2020-04-02 09:23
 */
public class WebApplicationContext {

    private DefaultWebBeanFactory defaultWebBeanFactory;

    private List<Filter> filterChains = new ArrayList<>(4);

    private List<HandlerInterceptor> handlerInterceptorChains =  new ArrayList<>(4);


    public WebApplicationContext(DefaultWebBeanFactory defaultWebBeanFactory) {
        this.defaultWebBeanFactory = defaultWebBeanFactory;
    }

    public void doRequest(HttpRequest request, HttpResponse response) {
        try {
            doFilter(request, response);
            doDispatcher(request, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void doFilter(HttpRequest request, HttpResponse response) {
        for (Filter filter : filterChains) {
            filter.doFilter(request, response);
        }
    }

    public void doDispatcher(HttpRequest request, HttpResponse response) throws Exception {
        MappingHandler handler = findHandler(request);;
        if (handler == null) {
            response.setMsg("<h2>Sorry 404</h2>");
            return;
        }
        for (HandlerInterceptor interceptor : handlerInterceptorChains) {
            boolean hasNext = interceptor.preHandle(request, response, handler);
            if (!hasNext) {
                return;
            }
        }
        Object result = handler.invokeRequestMethod();
        for (HandlerInterceptor interceptor : handlerInterceptorChains) {
            interceptor.postHandle(request, response, handler, null);
        }
        if (response.getMsg() == null && result != null) {
            response.setMsg(result.toString());
        }
    }

    public MappingHandler findHandler(HttpRequest request) {
        String url = request.getUrl();
        List<MappingHandler> list = new ArrayList<>(4);
        List<MappingHandler> mappingHandlerList = defaultWebBeanFactory.getMappingHandlerList();
        for (MappingHandler handler : mappingHandlerList) {
            if (handler.matchUrl(url)) {
                list.add(handler);
            }
        }
        if (list.size() > 1) {
            throw new RuntimeException("找到多个匹配方法");
        }
        return list.size() == 1 ? list.get(0) : null;
    }
}
