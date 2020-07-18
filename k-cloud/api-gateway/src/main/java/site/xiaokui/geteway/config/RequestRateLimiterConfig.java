package site.xiaokui.geteway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import reactor.core.publisher.Mono;

/**
 * 限流策略，可通过RequestRateLimiter.key-resolver: "#{@ipAddressKeyResolver}"的形式指定
 * @author hk
 */
@Configuration
public class RequestRateLimiterConfig {

    /**
     * 根据 HostName 进行限流
     */
	@Primary
    @Bean("ipAddressKeyResolver")
    public KeyResolver ipAddressKeyResolver() {
        return exchange -> Mono.just(exchange.getRequest().getRemoteAddress().getHostName());
    }

    /**
     * 根据api接口来限流
     */
    @Bean(name="apiKeyResolver")
    public KeyResolver apiKeyResolver() {
        return exchange ->  Mono.just(exchange.getRequest().getPath().value());
    }

    /**
     * 用户限流
     * 使用这种方式限流，请求路径中必须携带userId参数。
     */
    @Bean("userKeyResolver")
    KeyResolver userKeyResolver() {
        return exchange -> Mono.just(exchange.getRequest().getQueryParams().getFirst("userId"));
    }
}
