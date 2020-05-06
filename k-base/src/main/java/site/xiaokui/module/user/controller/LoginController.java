package site.xiaokui.module.user.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.xiaokui.common.exception.TooMuchPasswordRetryException;
import site.xiaokui.common.util.StringUtil;
import site.xiaokui.controller.BaseController;
import site.xiaokui.entity.ResultEntity;
import site.xiaokui.module.user.entity.SysUser;
import site.xiaokui.module.user.service.UserService;

import static site.xiaokui.module.user.UserConstants.REMEMBER_FLAG;

/**
 * @author HK
 * @date 2018-05-20 21:34
 */
@Slf4j
@RestController
@RequestMapping("/sys")
public class LoginController extends BaseController {

    @Autowired
    private UserService userService;

    /**
     * 注册用户
     */
    @PostMapping("/register")
    public ResultEntity register() {
        String username = this.getParameter("username");
        String email = this.getParameter("email");
        String password = this.getParameter("password").trim();
        if (StringUtil.hasEmpty(username, email, password)) {
            return ResultEntity.paramError();
        }
        email = email.trim();
        password = password.trim();
        if (!checkUsernamePass(username)) {
            return ResultEntity.error("用户名已被注册");
        }
        if (!checkEmailPass(email)) {
            return ResultEntity.error("邮箱已被注册");
        }

        SysUser user = this.initDefaultUser(username, email, password);
        String ip = this.getIP();
        user.setLastLoginIp(ip);

        boolean success = userService.insertIgnoreNull(user);
        // 一种更简单的写法：return returnResult(success)
        return returnResult(success, "新增用户失败");
    }

    /**
     * 点击登录执行的动作
     */
    @PostMapping("/login")
    public ResultEntity login() {
        String username = this.getParameter("loginName");
        String password = this.getParameter("password");
        String remember = this.getParameter("remember");

        // 自我感觉有点不厚道^_^,但是做人是要坚持原则的，果断改为debug
        log.debug("登录信息[username:{},password:{},remember:{}]", username, password, remember);

        // 允许为空格
        if (StringUtil.hasEmpty(username, password, remember)) {
            return ResultEntity.paramError();
        }
        password = password.trim();
        //验证码是否开启
//        if (KaptchaUtil.getKaptchaOnOff()) {
//            String kaptcha = this.getParameter("kaptcha").trim();
//            String code = (String) this.getSession().getAttribute(Constants.KAPTCHA_SESSION_KEY);
//            if (StringUtil.isEmpty(kaptcha) || !kaptcha.equalsIgnoreCase(code)) {
//                return ResultEntity.error("验证码错误");
//            }
//        }

        Subject currentUser = this.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(username, password.toCharArray(), this.getIP());
        if (REMEMBER_FLAG.equals(remember)) {
            token.setRememberMe(true);
        } else {
            token.setRememberMe(false);
        }

        try {
            currentUser.login(token);
        } catch (UnknownAccountException | IncorrectCredentialsException e) {
            return ResultEntity.error("账号或密码错误");
        } catch (LockedAccountException e) {
            return ResultEntity.error("账号被冻结");
        } catch (TooMuchPasswordRetryException e) {
            return ResultEntity.error(e.getMessage());
        }
        return ResultEntity.ok();
    }

    /**
     * 退出登录
     */
    @RequestMapping(value = "/logout")
    public ResultEntity logout() {
        this.getSubject().logout();
        return ResultEntity.ok();
    }

    private boolean checkUsernamePass(String username) {
        return userService.getUserByName(username) == null;
    }

    private boolean checkEmailPass(String email) {
        return userService.getUserByEmail(email) == null;
    }
}
