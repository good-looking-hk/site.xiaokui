package site.xiaokui.config.spring;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.AuthorizationException;
import org.springframework.core.annotation.Order;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import site.xiaokui.module.base.entity.ResultEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Map;

/**
 * 全局控制器异常拦截器，优先级越高值越低
 * 结果可返回一个对象（@ResponseBody），会转化成相应的字符串，亦可返回一个字符串，返回相应的界面，并携带信息
 *
 * @author HK
 * @date 2018-05-31 01:37
 */
@Slf4j
@ControllerAdvice
@Order(-1)
public class GlobalExceptionHandler {

    /**
     * 统一处理参数类型不匹配异常
     */
    @ExceptionHandler(NumberFormatException.class)
    @ResponseBody
    public ResultEntity paramError(NumberFormatException e) {
        return ResultEntity.paramError(e.getMessage());
    }

    /**
     * 空指针异常
     */
    @ExceptionHandler(NullPointerException.class)
    @ResponseBody
    public ResultEntity npe(NullPointerException e) {
        e.printStackTrace();
        return ResultEntity.error("空指针异常:" + e.getMessage());
    }

    /**
     * 没有权限
     */
    @ExceptionHandler(AuthorizationException.class)
    @ResponseBody
    public ResultEntity handleAuthorizationException() {
        return ResultEntity.error("暂无权限，请联系管理员！");
    }

    /**
     * get或post方法不匹配、方法参数不匹配
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseBody
    public ResultEntity notSupportedMethod() {
        return new ResultEntity(405, "不支持的请求格式");
    }

    /**
     * 拦截未知的运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    public ResultEntity notFound(RuntimeException e, HttpServletRequest request) {
        log.error("url[{}]访问出错,错误信息:{}", request.getRequestURI(), e.getCause());
        Map<String, String[]> map = request.getParameterMap();
        for (Map.Entry<String, String[]> m : map.entrySet()) {
            log.error(m.getKey() + ":" + Arrays.toString(m.getValue()));
        }
        e.printStackTrace();
        return ResultEntity.error(e.getMessage() + " cause by " + e.getCause());
    }

    /**
     * 拦截未知异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResultEntity exception(RuntimeException e, HttpServletRequest request) {
        e.printStackTrace();
        return ResultEntity.error(e.getMessage() + " cause by " + e.getCause());
    }
}