package site.xiaokui.common.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import site.xiaokui.common.aop.annotation.Log;
import site.xiaokui.config.shiro.ShiroKit;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;

/**
 * 日志切面
 * @author HK
 * @date 2018-10-03 15:36
 */
@Slf4j
@Aspect
@Component
public class LogAspect {

    private final Class[] aspectClass = {Log.class};

    @Pointcut(value = "@annotation(site.xiaokui.common.aop.annotation.Log)")
    public void cutService() {
    }

    @Around("cutService()")
    public Object cutService(ProceedingJoinPoint point) throws Throwable {
        Object result = null;
        Signature signature = point.getSignature();
        // 如果不是应用在方法级别
        if (!(signature instanceof MethodSignature)) {
            throw new IllegalArgumentException("该注解只能用于方法");
        }
        Method method = ((MethodSignature) signature).getMethod();
        // 留作扩展
        if (aspectClass.length == 1) {
            result = dealLog(point, method);
        } else {
            // TODO
        }
        return result;
    }

    private Object dealLog(ProceedingJoinPoint point, Method method) throws Throwable{
        Object target = null;
        Log annotation = method.getAnnotation(Log.class);
        boolean basic = annotation.basic();
        boolean statisticTime = annotation.statisticTime();
        boolean writeToDb = annotation.writeToDB();
        StringBuilder sb1 = null, sb2 = null, sb3 = null;
        if (basic) {
            sb1 = new StringBuilder(method.getDeclaringClass().getName());
            sb1.append(".").append(method.getName()).append("(").append(Arrays.toString(point.getArgs())).append(")");
        }
        if (statisticTime) {
            long startTime = System.currentTimeMillis();
            // 执行方法
            target = point.proceed();
            long duration = System.currentTimeMillis() - startTime;
            sb2 = new StringBuilder("耗时" + duration + "ms");
        }
        if (writeToDb) {
            // 只针对已登录用户做记录
            if (ShiroKit.getInstance().getUser() != null) {
                // TODO
            }
        }
        // 两者均不为空，则加个逗号，便于查看
        if (sb1 != null && sb2 != null) {
            sb3 = sb1.append(",").append(sb2);
        } else if (sb1 != null || sb2 != null) {
            sb3 = sb1 != null ? sb1 : sb2;
        }
        if (sb3 != null) {
            sb3.insert(0, "执行");
            log.info(sb3.toString());
        }
        return target;
    }
}
