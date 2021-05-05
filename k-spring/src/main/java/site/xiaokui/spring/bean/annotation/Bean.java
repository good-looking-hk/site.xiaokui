package site.xiaokui.spring.bean.annotation;

import java.lang.annotation.*;

/**
 * @author HK
 * @date 2020-04-01 10:52
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Bean {

    String name() default "";

}
