package site.xiaokui.blog.controller.remote;

import org.springframework.stereotype.Component;

/**
 * 测试Hystrix熔断器，当服务不可用时会跳转到这里
 * @author HK
 * @date 2020-06-28 15:30
 */
@Component
public class HystrixFallback implements RemoteTestController {

    @Override
    public String invokeRemoteTest(String msg) {
        return "远程调用失败，熔断器启动" + msg;
    }
}
