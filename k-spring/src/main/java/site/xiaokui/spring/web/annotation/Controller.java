package site.xiaokui.spring.web.annotation;

import java.lang.annotation.*;

/**
 * @author HK
 * @date 2020-03-26 14:23
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Controller {

    String name() default "";
}
