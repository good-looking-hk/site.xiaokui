package site.xiaokui.blog;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import site.xiaokui.BlogApp;

/**
 * @author HK
 * @date 2017/06/25 20:22
 */
public class WarApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(BlogApp.class);
    }
}
