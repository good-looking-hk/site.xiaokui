package site.xiaokui.oauth2.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import site.xiaokui.base.aop.annotation.Log;
import site.xiaokui.oauth2.service.ClientService;

import javax.servlet.http.HttpServletRequest;

/**
 * @author HK
 * @date 2020-06-16 10:35
 */
@Controller
public class IndexController {

    @Autowired
    private ClientService clientService;

    @GetMapping({"/", "/index"})
    public String index() {
        return "/index";
    }

    @GetMapping({"/thirdLogin"})
    public String thirdLogin(HttpServletRequest request, Model model) {
        String queryString = request.getQueryString();
        System.out.println(queryString);
        model.addAttribute("query", queryString);
        return "/thirdLogin";
    }
}
