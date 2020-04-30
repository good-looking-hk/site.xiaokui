package site.xiaokui.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.beetl.sql.core.SQLManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import site.xiaokui.aop.annotation.Log;
import site.xiaokui.entity.SysLog;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Date;

/**
 * 日志切面，除了通过AOP，还可以在Spring MVC中的拦截器实现统计耗时
 * @author HK
 * @date 2018-10-03 15:36
 */
@Slf4j
@Aspect
@Component
public class LogAspect {

    @Autowired
    private SQLManager sqlManager;

    /**
     * 扫描含有@Log注解的方法
     */
    @Pointcut(value = "@annotation(site.xiaokui.aop.annotation.Log)")
    public void cutService() {
    }

    @Around("cutService()")
    public Object cutService(ProceedingJoinPoint point) throws Throwable {
        Signature signature = point.getSignature();
        // 如果不是应用在方法级别
        if (!(signature instanceof MethodSignature)) {
            throw new IllegalArgumentException("该注解只能用于方法");
        }
        Method method = ((MethodSignature) signature).getMethod();
        return dealLog(point, method);
    }

    private Object dealLog(ProceedingJoinPoint point, Method method) throws Throwable{
        Object target = null;
        Object realObj = point.getTarget();
        MethodSignature msig = (MethodSignature) point.getSignature();;
        // 可以有效防止代理类的注解丢失问题
        Method currentMethod = realObj.getClass().getMethod(msig.getName(), msig.getParameterTypes());
        Log annotation = currentMethod.getAnnotation(Log.class);

        String remark = annotation.remark();
        boolean statisticTime = annotation.statisticTime();
        boolean recodeMethod = annotation.recodeMethod();
        boolean recodeMethodParams = annotation.recodeMethodParams();
        boolean recodeReturn = annotation.recodeReturn();

        boolean writeToDb = annotation.writeToDB();
        boolean inputToLog = annotation.inputToLog();

        long startTime = System.currentTimeMillis();
        // 执行方法
        target = point.proceed();
        long duration = System.currentTimeMillis() - startTime;

        StringBuilder sb = new StringBuilder("执行方法");
        if (recodeMethod) {
            sb.append(method);
        } else {
            sb.append(method.getName());
        }

        if (recodeMethodParams) {
            sb.append("[").append(Arrays.toString(point.getArgs())).append("]");
        }

        if (recodeReturn) {
            sb.append("返回结果").append(target);
        }

        if (inputToLog) {
            log.info(sb.toString());
        }

        if (statisticTime) {
            sb.append(",耗时").append(duration);
        }

        if (writeToDb) {
            SysLog sysLog = new SysLog();
            sysLog.setName(remark);
            sysLog.setContent(sb.toString());
            sysLog.setRemark("@Log写入");
            sysLog.setCreateTime(new Date());
            sqlManager.insert(SysLog.class, sysLog);
        }
        return target;
    }
}
