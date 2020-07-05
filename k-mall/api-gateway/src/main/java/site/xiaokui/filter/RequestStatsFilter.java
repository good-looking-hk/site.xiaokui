package site.xiaokui.filter;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import site.xiaokui.service.TokenService;

/**
 * @author hk
 */
@Slf4j
@Component
public class RequestStatsFilter implements GlobalFilter, Ordered {

    private static final String LOGIN_PATH = "/user-service/login";

    @Autowired
    private TokenService tokenService;

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 如果是登录请求，直接放行
        if (exchange.getRequest().getURI().getPath().equals(LOGIN_PATH)) {
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                // 如何获取返回内容
                // TODO
            }));
        }

        // 暂时只支持get地址上面的token检查
        String token = exchange.getRequest().getQueryParams().getFirst("token");
        if (StringUtils.isEmpty(token) || !tokenService.checkToken(token)) {
            // 不合法的token
            ServerHttpResponse response = exchange.getResponse();
            // 设置headers
            HttpHeaders httpHeaders = response.getHeaders();
            httpHeaders.add("Content-Type", "application/text; charset=UTF-8");
            // 设置body
            log.info("拦截请求:" + exchange.getRequest().getURI());
            String warningStr = "未登录或登录过期，请重新登录";
            DataBuffer bodyDataBuffer = response.bufferFactory().wrap(warningStr.getBytes());
            return response.writeWith(Mono.just(bodyDataBuffer));
        }

        exchange.getAttributes().put("beginTime", System.currentTimeMillis());
        // token有效
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            long beginTime = exchange.getAttributeOrDefault("beginTime", 0L);
            log.info("请求url:" + exchange.getRequest().getURI() + "请求id:" + exchange.getRequest().getId() + ",耗时:" + (System.currentTimeMillis() - beginTime) / 1000 + "ms");
        }));
    }

}
