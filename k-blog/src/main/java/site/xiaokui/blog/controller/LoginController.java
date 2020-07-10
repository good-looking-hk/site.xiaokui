package site.xiaokui.blog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import site.xiaokui.base.controller.BaseController;
import site.xiaokui.blog.config.shiro.ShiroKit;


/**
 * @author HK
 * @date 2018-05-23 14:39
 */
@Controller("rootLoginController")
public class LoginController extends BaseController {

    /**
     * 跳转到登录页面
     */
    @GetMapping("/login")
    public String login() {
        // 如果用户已登录，将跳至管理界面
        if (ShiroKit.getInstance().getSubject().isAuthenticated()) {
            return REDIRECT + "/manage";
        }
        return "/sys/login";
    }

    /**
     * 注册界面
     */
    @GetMapping("/register")
    public String register() {
        if (ShiroKit.getInstance().getSubject().isAuthenticated()) {
            return REDIRECT + "/manage";
        }
        return  "/sys/register";
    }

    /**
     * 后台管理界面，只有通过认证才能访问
     */
    @GetMapping("/manage")
    public String manage(Model model) {
//        List<SysMenu> list = menuService.getUserMenu(this.getRoleId());
//        model.addAttribute("menu", list);
//        return SYS_PREFIX + "/manage";
        return null;
    }

    @GetMapping("/unauthorized")
    public String unauthorized() {
        return "/unauthorized";
    }
}
