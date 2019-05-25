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
    public ResultEntity notFount(RuntimeException e, HttpServletRequest request) {
        if (log.isDebugEnabled()) {
            log.debug("url[{}]访问出错", request.getRequestURI());
            Map<String, String[]> map =  request.getParameterMap();
            for (Map.Entry<String, String[]> m : map.entrySet()) {
                log.debug(m.getKey() + ":" + Arrays.toString(m.getValue()));
            }
            e.printStackTrace();
        }
        return ResultEntity.error(e.getMessage() + " cause by " + e.getCause());
    }

//    /**
//     * 拦截业务异常
//     */
//    @ExceptionHandler(GunsException.class)
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    @ResponseBody
//    public ErrorTip notFount(GunsException e) {
//        LogManager.me().executeLog(LogTaskFactory.exceptionLog(ShiroKit.getUser().getId(), e));
//        getRequest().setAttribute("tip", e.getMessage());
//        log.error("业务异常:", e);
//        return new ErrorTip(e.getCode(), e.getMessage());
//    }
//
//    /**
//     * 用户未登录异常
//     */
//    @ExceptionHandler(AuthenticationException.class)
//    @ResponseStatus(HttpStatus.UNAUTHORIZED)
//    public String unAuth(AuthenticationException e) {
//        log.error("用户未登陆：", e);
//        return "/login.html";
//    }
//
//    /**
//     * 账号被冻结异常
//     */
//    @ExceptionHandler(DisabledAccountException.class)
//    @ResponseStatus(HttpStatus.UNAUTHORIZED)
//    public String accountLocked(DisabledAccountException e, Model model) {
//        String username = getRequest().getParameter("username");
//        LogManager.me().executeLog(LogTaskFactory.loginLog(username, "账号被冻结", getIp()));
//        model.addAttribute("tips", "账号被冻结");
//        return "/login.html";
//    }

}