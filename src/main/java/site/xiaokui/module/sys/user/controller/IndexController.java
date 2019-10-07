package site.xiaokui.module.sys.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import site.xiaokui.common.util.StringUtil;
import site.xiaokui.module.base.controller.BaseController;
import site.xiaokui.module.sys.blog.BlogConstants;
import site.xiaokui.module.sys.blog.util.BlogUtil;
import site.xiaokui.module.sys.user.UserConstants;
import site.xiaokui.module.sys.user.entity.SysMenu;
import site.xiaokui.module.sys.user.service.MenuService;

import java.io.File;
import java.util.List;

/**
 * @author HK
 * @date 2018-05-23 23:20
 */
@Controller("INDEX")
public class IndexController extends BaseController {

    @Autowired
    private MenuService menuService;

    /**
     * SYS_PREFIX字段默认为 /sys
     */
    private static final String SYS_PREFIX = UserConstants.SYS_PREFIX;

    @GetMapping({"/", "/index"})
    public String index() {
        return FORWARD + "/blog";
    }

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
