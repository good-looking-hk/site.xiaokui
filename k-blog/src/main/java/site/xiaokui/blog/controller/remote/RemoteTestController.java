package site.xiaokui.blog.controller.remote;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author HK
 * @date 2020-06-28 10:02
 */
@FeignClient(name = "auth-server", fallback = HystrixFallback.class)
public interface RemoteTestController {

    @PostMapping("/s/test")
    @ResponseBody
    String invokeRemoteTest(@RequestParam String msg);

}
