package site.xiaokui.user.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.xiaokui.base.entity.ResultEntity;
import site.xiaokui.base.util.StringUtil;
import site.xiaokui.common.exception.TooMuchPasswordRetryException;
import site.xiaokui.user.entity.SysMenu;
import site.xiaokui.user.entity.SysUser;
import site.xiaokui.user.service.MenuService;
import site.xiaokui.user.service.UserService;

import java.util.List;

import static site.xiaokui.user.UserConstants.REMEMBER_FLAG;
import static site.xiaokui.user.UserConstants.SYS_PREFIX;

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

    @Autowired
    private MenuService menuService;

    /**
     * 跳转到登录页面
     */
    @GetMapping("/login")
    public String login() {
        // 如果用户已登录，将跳至管理界面
        if (this.getSubject().isAuthenticated()) {
            return REDIRECT + "/manage";
        }
        return SYS_PREFIX + "/login";
    }

    /**
     * 注册界面
     */
    @GetMapping("/register")
    public String register() {
        if (this.getSubject().isAuthenticated()) {
            return REDIRECT + "/manage";
        }
        return SYS_PREFIX + "/register";
    }

    /**
     * 后台管理界面，只有通过认证才能访问
     */
    @GetMapping("/manage")
    public String manage(Model model) {
        List<SysMenu> list = menuService.getUserMenu(this.getRoleId());
        model.addAttribute("menu", list);
        return SYS_PREFIX + "/manage";
    }

    @GetMapping("/unauthorized")
    public String unauthorized() {
        return "/unauthorized";
    }

    /**
     * 注册用户
     */
    @PostMapping("/register")
    public ResultEntity register(String username, String email, String password) {
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
        // TODO 获取ip方法
        // String ip = this.getIP();
        user.setLastLoginIp(null);

        boolean success = userService.insertIgnoreNull(user);
        // 一种更简单的写法：return returnResult(success)
        return returnResult(success, "新增用户失败");
    }

    /**
     * 点击登录执行的动作
     */
    @PostMapping("/login")
    public ResultEntity login(String loginName, String password, String remember) {
        String username = loginName;
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
//        UsernamePasswordToken token = new UsernamePasswordToken(username, password.toCharArray(), this.getIP());
        // TODO ip实现
        UsernamePasswordToken token = new UsernamePasswordToken(username, password.toCharArray(), null);

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
