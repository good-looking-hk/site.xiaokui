package site.xiaokui.spring.web;

import site.xiaokui.spring.web.server.HttpRequest;
import site.xiaokui.spring.web.server.HttpResponse;

/**
 * @author HK
 * @date 2020-04-02 11:11
 */
public interface Filter {

    void init();

    void doFilter(HttpRequest request, HttpResponse response);

    void destory();

}
