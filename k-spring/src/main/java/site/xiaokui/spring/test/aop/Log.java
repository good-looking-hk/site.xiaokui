package site.xiaokui.spring.test.aop;

import java.lang.annotation.*;

/**
 * @author HK
 * @date 2020-04-11 11:29
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
@Inherited
public @interface Log {

    /**
     * 记录方法耗时
     */
    boolean statisticTime() default true;
}

