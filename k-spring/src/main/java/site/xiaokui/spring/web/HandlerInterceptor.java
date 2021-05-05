package site.xiaokui.spring.web;

import site.xiaokui.spring.web.server.HttpRequest;
import site.xiaokui.spring.web.server.HttpResponse;

/**
 * @author HK
 * @date 2020-04-02 11:24
 */
public interface HandlerInterceptor {

    default boolean preHandle(HttpRequest request, HttpResponse response, Object handler) throws Exception {
        return true;
    }

    default void postHandle(HttpRequest request, HttpResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    default void afterCompletion(HttpRequest request, HttpResponse response, Object handler, Exception ex) throws Exception {
    }
}
