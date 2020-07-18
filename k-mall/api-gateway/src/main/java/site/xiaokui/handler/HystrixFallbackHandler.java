package site.xiaokui.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * Hystrix降级处理
 */
@Slf4j
@Component
@SuppressWarnings("all")
public class HystrixFallbackHandler implements HandlerFunction<ServerResponse> {

     
    @Override
    public Mono<ServerResponse> handle(ServerRequest serverRequest) {
    	
        serverRequest.attribute(ServerWebExchangeUtils.GATEWAY_ORIGINAL_REQUEST_URL_ATTR)
            .ifPresent(originalUrls -> log.error("网关执行请求:{}失败,hystrix服务降级处理", originalUrls));

        Map resp = new HashMap();
        resp.put("code", "9999");
        resp.put("msg", "网关错误");
        return ServerResponse
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromObject(resp));
    }
}
