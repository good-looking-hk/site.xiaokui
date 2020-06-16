package site.xiaokui.base.aop.annotation;

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
     * 此次日志备注，一般来说必填
     */
    String remark() default "";

    /**
     * 系统模块名称
     */
    String module() default "";

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
    boolean recordMethod() default false;

    /**
     * 记录方法入参
     */
    boolean recordMethodParams() default false;

    /**
     * 记录方法返回值
     */
    boolean recordReturn() default false;

    /**
     * 写到数据库
     */
    boolean writeToDB() default false;

    /**
     * 是否记录ip，如果有的话
     */
    boolean recordIp() default false;
}
