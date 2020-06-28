package site.xiaokui.blog.controller.remote;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import site.xiaokui.base.aop.annotation.Log;

/**
 * @author HK
 * @date 2020-06-28 10:13
 */
@Controller
public class TestController {

    @Autowired
    private RemoteTestController remoteTestController;

    @Autowired
    private Environment environment;

    @Log(module="k-blog", remark="测试调用远程auth-server服务", recordMethodParams = true)
    @GetMapping("/s/test")
    @ResponseBody
    public String test(String msg) {
        return remoteTestController.invokeRemoteTest(msg);
    }

    @Log(module="k-blog", remark="返回测试消息")
    @PostMapping("/s/test")
    @ResponseBody
    public String anyName(String msg) {
        String ip = environment.getProperty("spring.cloud.client.ip-address");
        String port = environment.getProperty("server.port");
        String application = environment.getProperty("spring.application.name");
        return ip + ":" + port + ":" + application + "返回消息:" + msg;
    }
}
