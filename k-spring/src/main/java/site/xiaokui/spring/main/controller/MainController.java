package site.xiaokui.spring.main.controller;

import site.xiaokui.spring.web.annotation.Controller;
import site.xiaokui.spring.web.annotation.RequestMapping;

/**
 * @author HK
 * @date 2020-04-01 09:37
 */
@RequestMapping
@Controller
public class MainController {

    @RequestMapping("/index")
    public String index() {
        return "hello my spring";
    }

    @RequestMapping(value = {"/test", "/aaaa"})
    public String test() {
        return "this is test page";
    }
}
