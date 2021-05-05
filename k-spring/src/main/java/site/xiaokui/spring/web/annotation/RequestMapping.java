package site.xiaokui.spring.web.annotation;

import java.lang.annotation.*;

/**
 * @author HK
 * @date 2020-04-01 09:38
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestMapping {

    String[] value() default {};

}
