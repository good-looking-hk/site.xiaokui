package site.xiaokui.aop.annotation;

import site.xiaokui.aop.LogType;

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

    /**
     * 此次日志备注
     */
    String remark() default "";

    /**
     * 单纯记录方法耗时
     */
    boolean statisticTime() default true;

    /**
     * 通过log打印日志
     */
    boolean inputToLog() default true;

    /**
     * 记录方法名称，区别如下
     * public final void java.lang.Object.wait(long,int) throws java.lang.InterruptedException
     * wait
     */
    boolean recodeMethod() default false;

    /**
     * 记录方法入参
     */
    boolean recodeMethodParams() default false;

    /**
     * 记录方法返回值
     */
    boolean recodeReturn() default false;

    /**
     * 写到数据库
     */
    boolean writeToDB() default false;

    /**
     * 留作扩展
     */
    LogType type() default LogType.NONE;
}
