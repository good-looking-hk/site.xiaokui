package site.xiaokui.common.aop;

import org.apache.shiro.authz.AuthorizationException;
import org.springframework.core.annotation.Order;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import site.xiaokui.module.base.entity.ResultEntity;

/**
 * @author HK
 * @date 2018-05-31 01:37
 */
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

    @ExceptionHandler(NullPointerException.class)
    @ResponseBody
    public ResultEntity npe(NullPointerException e) {
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
     * get或post方法不匹配
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseBody
    public ResultEntity notSupportedMethod() {
        return new ResultEntity(405, "不支持的请求格式");
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
//
//    /**
//     * 账号密码错误异常
//     */
//    @ExceptionHandler(CredentialsException.class)
//    @ResponseStatus(HttpStatus.UNAUTHORIZED)
//    public String credentials(CredentialsException e, Model model) {
//        String username = getRequest().getParameter("username");
//        LogManager.me().executeLog(LogTaskFactory.loginLog(username, "账号密码错误", getIp()));
//        model.addAttribute("tips", "账号密码错误");
//        return "/login.html";
//    }
//
//    /**
//     * 验证码错误异常
//     */
//    @ExceptionHandler(InvalidKaptchaException.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public String credentials(InvalidKaptchaException e, Model model) {
//        String username = getRequest().getParameter("username");
//        LogManager.me().executeLog(LogTaskFactory.loginLog(username, "验证码错误", getIp()));
//        model.addAttribute("tips", "验证码错误");
//        return "/login.html";
//    }
//
//    /**
//     * 无权访问该资源异常
//     */
//    @ExceptionHandler(UndeclaredThrowableException.class)
//    @ResponseStatus(HttpStatus.UNAUTHORIZED)
//    @ResponseBody
//    public ErrorTip credentials(UndeclaredThrowableException e) {
//        getRequest().setAttribute("tip", "权限异常");
//        log.error("权限异常!", e);
//        return new ErrorTip(BizExceptionEnum.NO_PERMITION.getCode(), BizExceptionEnum.NO_PERMITION.getMessage());
//    }
//
//    /**
//     * 拦截未知的运行时异常
//     */
//    @ExceptionHandler(RuntimeException.class)
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    @ResponseBody
//    public ErrorTip notFount(RuntimeException e) {
//        LogManager.me().executeLog(LogTaskFactory.exceptionLog(ShiroKit.getUser().getId(), e));
//        getRequest().setAttribute("tip", "服务器未知运行时异常");
//        log.error("运行时异常:", e);
//        return new ErrorTip(BizExceptionEnum.SERVER_ERROR.getCode(), BizExceptionEnum.SERVER_ERROR.getMessage());
//    }
}