package site.xiaokui.handler;

import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.DefaultErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RequestPredicate;


/**
 * 参考链接：https://www.jianshu.com/p/6f631f3e00b9
 * 全局异常处理
 *
 * @author hk
 */
@Component
public class GlobalExceptionHandler extends DefaultErrorWebExceptionHandler {

    public GlobalExceptionHandler(ErrorAttributes errorAttributes, ResourceProperties resourceProperties, ErrorProperties errorProperties, ApplicationContext applicationContext) {
        super(errorAttributes, resourceProperties, errorProperties, applicationContext);
    }

    /**
     * 默认返回json格式，不返回html错误页面
     */
    @Override
    protected RequestPredicate acceptsTextHtml() {
        return (serverRequest) -> false;
    }

}

