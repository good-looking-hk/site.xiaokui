package site.xiaokui.blog.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import site.xiaokui.base.controller.BaseController;
import site.xiaokui.blog.service.UserService;

/**
 * 目前使用oauth2登录
 * @author HK
 * @date 2018-05-20 21:34
 */
@Slf4j
@RequestMapping("/sys")
public class LoginController extends BaseController {

    @Autowired
    private UserService userService;

    /**
     * 点击登录执行的动作
     */
    @PostMapping("/login")
    public String login() {
        return "11";
    }

    /**
     * 退出登录
     */
    @RequestMapping(value = "/logout")
    public String logout() {
        return "22";
    }
}
