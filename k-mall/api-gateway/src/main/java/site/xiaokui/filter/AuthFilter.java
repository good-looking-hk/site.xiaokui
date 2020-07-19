package site.xiaokui.filter;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import site.xiaokui.Constants;
import site.xiaokui.service.TokenService;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 认证过滤器，拦截所有非法请求，主要是以header中的token作为筛选条件，在LogFilter前面
 *
 * @author hk
 */
@Component
public class AuthFilter implements GlobalFilter, Ordered {

    private Logger logger = LoggerFactory.getLogger(AuthFilter.class);

    private static final String LOGIN_PATH = "/user-service/login";

    @Autowired
    private TokenService tokenService;

    @Override
    public int getOrder() {
        return Constants.AUTH_FILTER_ORDER;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 如果是登录请求，直接放行
        if (exchange.getRequest().getURI().getPath().equals(LOGIN_PATH)) {
            return chain.filter(exchange);
        }

        // get地址上面的token检查
        String token = exchange.getRequest().getQueryParams().getFirst("token");
        if (StringUtils.isEmpty(token)) {
            // 再取header中的token
            token = exchange.getRequest().getHeaders().getFirst("token");
        }

        if (StringUtils.isEmpty(token) || !tokenService.checkToken(token)) {
            // 不合法的token
            ServerHttpResponse response = exchange.getResponse();
            // 设置headers
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            HttpHeaders httpHeaders = response.getHeaders();
            httpHeaders.add("Content-Type", "application/json; charset=UTF-8");
            logger.warn("拦截信息 - url:" + exchange.getRequest().getURI() + ", method:" + exchange.getRequest().getMethod() + ", host:" + exchange.getRequest().getHeaders().getOrigin());
            // 设置body
            Map<String, Object> map = new HashMap<>(4);
            map.put("code", HttpStatus.UNAUTHORIZED.value());
            map.put("msg", "未登录或登录过期，请重新登录");
            map.put("datetime", DateUtil.format(new Date(), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")));
            DataBuffer bodyDataBuffer = response.bufferFactory().wrap(new JSONObject(map).toString().getBytes());
            return response.writeWith(Mono.just(bodyDataBuffer));
        }
        return chain.filter(exchange);
    }
}
