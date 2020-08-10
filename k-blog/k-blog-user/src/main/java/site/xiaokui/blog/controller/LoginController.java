package site.xiaokui.blog.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import site.xiaokui.base.entity.ResultEntity;
import site.xiaokui.base.entity.SysMenu;
import site.xiaokui.base.util.StringUtil;
import site.xiaokui.blog.constant.UserConstants;
import site.xiaokui.blog.entity.SysUser;
import site.xiaokui.blog.exception.TooMuchPasswordRetryException;
import site.xiaokui.blog.service.MenuService;
import site.xiaokui.blog.service.UserService;

import java.util.List;

/**
 * @author HK
 * @date 2018-05-20 21:34
 */
@Slf4j
@Controller
@RequestMapping("/sys")
public class LoginController extends BaseController {

    private static final String SYS_PREFIX = "/sys";

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
            return REDIRECT + SYS_PREFIX + "/manage";
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
        System.out.println(list.size());
        list.forEach(e -> {
            System.out.println(e);
        });
        return SYS_PREFIX + "/manage";
    }

    @GetMapping("/unauthorized")
    public String unauthorized() {
        return "/unauthorized";
    }

    /**
     * 注册用户
     */
    @ResponseBody
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
        user.setLastLoginIp(this.getIP());

        boolean success = userService.insertIgnoreNull(user);
        return returnResult(success, "新增用户失败");
    }

    /**
     * 点击登录执行的动作
     */
    @ResponseBody
    @PostMapping("/login")
    public ResultEntity login(String loginName, String password, String remember) {
        String username = loginName.trim();
        log.info("登录信息[username:{},password:{},remember:{}]", username, password, remember);

        // 允许为空格
        if (StringUtil.hasEmpty(username, password, remember)) {
            return ResultEntity.paramError();
        }
        password = password.trim();
        Subject currentUser = this.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(username, password.toCharArray(), this.getIP());
        if (UserConstants.REMEMBER_FLAG.equals(remember)) {
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
        } catch (Exception e) {
            log.error("登录失败，异常信息", e);
            return ResultEntity.error(e.getMessage());
        }
        return ResultEntity.ok();
    }

    /**
     * 退出登录
     */
    @ResponseBody
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
