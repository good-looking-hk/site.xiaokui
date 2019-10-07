package site.xiaokui.common.aop.annotation;

import site.xiaokui.common.aop.LogType;

import java.lang.annotation.*;

/**
 * 日志注解，只能用于方法层面
 * @author HK
 * @date 2018-10-03 15:41
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface  Log {

    String name() default "系统日志";

    LogType type() default LogType.NONE;

    /**
     * 输出基本信息，不建议修改
     */
    boolean basic() default true;

    /**
     * 记录方法耗时
     */
    boolean statisticTime() default true;

    /**
     * 写到数据库
     */
    boolean writeToDB() default false;
}
