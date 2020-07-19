package site.xiaokui;

/**
 * @author HK
 * @date 2020-07-19 19:27
 */
public class Constants {

    /**
     * 日志过滤器拦截顺序，值越小越先执行（于pre阶段而言、post阶段是反过来的，可先不管post阶段）
     * 让最基础的放在最前面，拦截/过滤部分无用请求，直接抛出429 too many request
     */
    public static final int LOG_FILTER_ORDER = 20;

    /**
     * 认证过滤器拦截顺序
     */
    public static final int AUTH_FILTER_ORDER = 10;

    /**
     * Redis限流顺序，由于 {@link org.springframework.cloud.gateway.filter.factory.RequestRateLimiterGatewayFilterFactory}没有实现Ordered接口，所以默认采取从0递增，即第一个为1
     */
    public static final int REDIS_LIMIT_ORDER = 1;
}
