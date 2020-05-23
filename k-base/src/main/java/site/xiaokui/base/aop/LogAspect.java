package site.xiaokui.base.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.beetl.sql.core.SQLManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import site.xiaokui.base.aop.annotation.Log;
import site.xiaokui.common.support.HttpUtil;
import site.xiaokui.entity.SysLog;

import javax.servlet.http.HttpServletRequest;
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
    @Pointcut(value = "@annotation(site.xiaokui.base.aop.annotation.Log)")
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
        Object target;
        Object realObj = point.getTarget();
        MethodSignature msig = (MethodSignature) point.getSignature();;
        // 可以有效防止代理类的注解丢失问题
        Method currentMethod = realObj.getClass().getMethod(msig.getName(), msig.getParameterTypes());
        Log annotation = currentMethod.getAnnotation(Log.class);

        String remark = annotation.remark();
        boolean statisticTime = annotation.statisticTime();
        boolean recordMethod = annotation.recordMethod();
        boolean recordMethodParams = annotation.recordMethodParams();
        boolean recordReturn = annotation.recordReturn();

        boolean writeToDb = annotation.writeToDB();
        boolean inputToLog = annotation.inputToLog();
        boolean recordIp = annotation.recordIp();

        long startTime = System.currentTimeMillis();
        // 执行方法
        target = point.proceed();
        long duration = System.currentTimeMillis() - startTime;
        
        String ip = "";
        if (recordIp) {
            ip = HttpUtil.getIP() + " ";
        }
        StringBuilder sb = new StringBuilder(ip + remark +  " 执行方法");
        if (recordMethod) {
            sb.append(method);
        } else {
            sb.append(method.getName());
        }

        if (recordMethodParams) {
            sb.append("[").append(Arrays.toString(point.getArgs())).append("]");
        }

        if (recordReturn) {
            sb.append("返回结果").append(target);
        }

        if (statisticTime) {
            sb.append("，耗时").append(duration).append("ms");
        }

        if (inputToLog) {
            log.info(sb.toString());
        }

        if (writeToDb) {
            SysLog sysLog = new SysLog();
            sysLog.setName("@Log写入");
            sysLog.setContent(sb.toString());
            sysLog.setRemark(remark);
            sysLog.setCreateTime(new Date());
            sqlManager.insert(SysLog.class, sysLog);
        }
        return target;
    }
}
