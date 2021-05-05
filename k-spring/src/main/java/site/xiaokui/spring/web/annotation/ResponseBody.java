package site.xiaokui.spring.web.annotation;

import java.lang.annotation.*;

/**
 * @author HK
 * @date 2020-04-03 15:37
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ResponseBody {
}
