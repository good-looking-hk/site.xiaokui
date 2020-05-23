package site.xiaokui.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import site.xiaokui.module.user.entity.SysMenu;
import site.xiaokui.module.user.service.MenuService;

import java.util.List;

import static site.xiaokui.module.user.UserConstants.SYS_PREFIX;

/**
 * @author HK
 * @date 2018-05-23 14:39
 */
@Controller("rootLoginController")
public class LoginController extends BaseController{

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
}
