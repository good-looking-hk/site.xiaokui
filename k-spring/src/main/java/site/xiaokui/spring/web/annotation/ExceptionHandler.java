package site.xiaokui.spring.web.annotation;

import java.lang.annotation.*;

/**
 * @author HK
 * @date 2020-04-03 15:39
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExceptionHandler {
}
