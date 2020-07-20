package site.xiaokui.controller;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;

import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 测试
 * @author HK
 * @date 2020-07-01 13:59
 */
@RestController
public class TestController {

    /**
     * 测试命令 curl -X POST -d 'name=hk' 'http://127.0.0.1:8000/hello'
     */
    @RequestMapping("/hello")
    public String hello(String name) {
        return "hello," + name;
    }
}
