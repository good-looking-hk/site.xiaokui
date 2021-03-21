package site.xiaokui.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author HK
 * @date 2021-03-18 11:11
 */
@Slf4j
@Configuration
public class RequestInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 拦截掉烦人的广告推广工具
        if (request.getRequestURI().equals("/")) {
            String referer = request.getHeader("referer");
            if (referer != null) {
                if (referer.contains("baidu") || referer.contains("sogou")) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    log.info("拦截点广告推送请求: referer={}" + referer);
                    return false;
                }
            }
            return true;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    }
}
