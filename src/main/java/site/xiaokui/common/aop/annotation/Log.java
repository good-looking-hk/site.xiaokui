package site.xiaokui.common.aop.annotation;

import java.lang.annotation.*;

/**
 * 日志注解
 * @author HK
 * @date 2018-10-03 15:41
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface  Log {

    /**
     * 输出基本信息，不建议修改
     */
    boolean basic() default true;

    /**
     * 记录耗时
     */
    boolean statisticTime() default true;

    /**
     * 写到数据库
     */
    boolean writeToDB() default false;
}
