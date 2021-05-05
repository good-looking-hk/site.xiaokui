package site.xiaokui.weixin.mp.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author HK
 * @date 2021-04-24 15:16
 */
@RestController
@RequestMapping("/wx")
public class TestController {

    @GetMapping("/test")
    public String test() {
        return "this wx is test str";
    }
}
