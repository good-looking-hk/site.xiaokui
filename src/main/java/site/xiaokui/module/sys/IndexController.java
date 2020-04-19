package site.xiaokui.module.sys;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import redis.clients.jedis.Jedis;
import site.xiaokui.CacheCenter;
import site.xiaokui.common.util.StringUtil;
import site.xiaokui.module.base.controller.BaseController;
import site.xiaokui.module.base.entity.ResultEntity;
import site.xiaokui.module.base.service.RedisService;
import site.xiaokui.module.sys.user.UserConstants;
import site.xiaokui.module.sys.user.entity.SysMenu;
import site.xiaokui.module.sys.user.service.MenuService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 项目根控制器，匹配 /*
 * @author HK
 * @date 2018-05-23 23:20
 */
@Controller("INDEX")
public class IndexController extends BaseController {

    @Autowired
    private CacheCenter cacheCenter;

    @Autowired
    private MenuService menuService;

    /**
     * SYS_PREFIX字段默认为 /sys
     */
    private static final String SYS_PREFIX = UserConstants.SYS_PREFIX;

    @GetMapping({"/", "/index"})
    public String index() {
        String index = cacheCenter.getSysConfigCache().getIndex();
        if (StringUtil.isNotBlank(index)) {
            return FORWARD + index;
        }
        return FORWARD + "/blog";
    }

    @RequestMapping(value = "/clearCache", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public String clearCache() {
        cacheCenter.clearSysConfigCache();
        return "重新载入配置成功";
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
