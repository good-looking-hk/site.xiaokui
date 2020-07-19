package site.xiaokui.filter;

import org.apache.commons.lang.StringUtils;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.factory.rewrite.CachedBodyOutputMessage;
import org.springframework.cloud.gateway.filter.factory.rewrite.ModifyRequestBodyGatewayFilterFactory;
import org.springframework.cloud.gateway.support.BodyInserterContext;
import org.springframework.cloud.gateway.support.DefaultServerRequest;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import site.xiaokui.Constants;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 全局拦截请求、应答信息，打印日志，应该放置在 认证过滤器{@link AuthFilter} 后面，避免过多的无效日志
 * 后期可以针对这里的日志信息做分析，比如引入ELK
 * @author HK
 * @date 2020-07-19 10:21
 */
@Component
public class LogFilter implements GlobalFilter, Ordered {

    private Logger logger = LoggerFactory.getLogger(AuthFilter.class);

    /**
     * 优先级默认设置为最高
     */
    @Override
    public int getOrder() {
        return Constants.LOG_FILTER_ORDER;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 获取用户传来的数据类型
        MediaType mediaType = exchange.getRequest().getHeaders().getContentType();
        ServerRequest serverRequest = new DefaultServerRequest(exchange);

        // 统计请求应答耗时
        exchange.getAttributes().put("beginTime", System.currentTimeMillis());

        // 获取response返回数据，初始化装饰器
        ServerHttpResponse originalResponse = exchange.getResponse();
        DataBufferFactory bufferFactory = originalResponse.bufferFactory();
        ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                if (body instanceof Flux) {
                    Flux<? extends DataBuffer> fluxBody = Flux.from(body);
                    return super.writeWith(fluxBody.buffer().map(dataBuffers -> {// 解决返回体分段传输
                        List<String> list = new ArrayList<>();
                        dataBuffers.forEach(dataBuffer -> {
                            byte[] content = new byte[dataBuffer.readableByteCount()];
                            dataBuffer.read(content);
                            DataBufferUtils.release(dataBuffer);
                            try {
                                list.add(new String(content, "utf-8"));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                        String bodyString = "";
                        for (String string : list) {
                            bodyString = bodyString + string;
                        }
                        logger.info("应答信息 - " + bodyString);
                        byte[] uppedContent = new String(bodyString.getBytes(), Charset.forName("UTF-8")).getBytes();
                        originalResponse.getHeaders().setContentLength(uppedContent.length);
                        return bufferFactory.wrap(uppedContent);
                    }));
                }
                // if body is not a flux. never got there.
                return super.writeWith(body);
            }
        };

        // 如果是json格式，将body内容转化为object or map 都可
        if (MediaType.APPLICATION_JSON.isCompatibleWith(mediaType)) {
            Mono<Object> modifiedBody = serverRequest.bodyToMono(Object.class)
                    .flatMap(body -> {
                        recordLog(exchange.getRequest(), body, true);
                        return Mono.just(body);
                    });
            return getVoidMono(exchange, chain, Object.class, modifiedBody, decoratedResponse).then(Mono.fromRunnable(() -> {
                logTime(exchange);
            }));
        }
        // 如果是表单请求
        else if (MediaType.APPLICATION_FORM_URLENCODED.isCompatibleWith(mediaType)) {
            Mono<String> modifiedBody = serverRequest.bodyToMono(String.class)
                    .flatMap(body -> {
                        recordLog(exchange.getRequest(), body, true);
                        return Mono.just(body);
                    });
            return getVoidMono(exchange, chain, String.class, modifiedBody, decoratedResponse).then(Mono.fromRunnable(() -> {
                logTime(exchange);
            }));
        }
        // TODO 这里未来还可以限制一些格式
        // 无法兼容的请求，则不读取body
        recordLog(exchange.getRequest(), "", true);

        // replace response with decorator
        return chain.filter(exchange.mutate().response(decoratedResponse).build()).then(Mono.fromRunnable(() -> {
            logTime(exchange);
        }));
    }

    /**
     * 参照 ModifyRequestBodyGatewayFilterFactory.java 截取的方法
     * {@link org.springframework.cloud.gateway.filter.factory.rewrite.ModifyRequestBodyGatewayFilterFactory#apply(ModifyRequestBodyGatewayFilterFactory.Config config)}
     */
    private Mono<Void> getVoidMono(ServerWebExchange exchange, GatewayFilterChain chain, Class outClass, Mono<?> modifiedBody, ServerHttpResponseDecorator decoratedResponse) {
        BodyInserter bodyInserter = BodyInserters.fromPublisher(modifiedBody, outClass);
        HttpHeaders headers = new HttpHeaders();
        headers.putAll(exchange.getRequest().getHeaders());

        // the new content type will be computed by bodyInserter
        // and then set in the request decorator
        headers.remove(HttpHeaders.CONTENT_LENGTH);

        CachedBodyOutputMessage outputMessage = new CachedBodyOutputMessage(exchange, headers);
        return bodyInserter.insert(outputMessage, new BodyInserterContext())
                .then(Mono.defer(() -> {
                    ServerHttpRequestDecorator decorator = new ServerHttpRequestDecorator(
                            exchange.getRequest()) {
                        @Override
                        public HttpHeaders getHeaders() {
                            long contentLength = headers.getContentLength();
                            HttpHeaders httpHeaders = new HttpHeaders();
                            httpHeaders.putAll(super.getHeaders());
                            if (contentLength > 0) {
                                httpHeaders.setContentLength(contentLength);
                            } else {
                                // TODO: this causes a 'HTTP/1.1 411 Length Required' on httpbin.org
                                httpHeaders.set(HttpHeaders.TRANSFER_ENCODING, "chunked");
                            }
                            return httpHeaders;
                        }

                        @Override
                        public Flux<DataBuffer> getBody() {
                            return outputMessage.getBody();
                        }
                    };
                    return chain.filter(exchange.mutate().request(decorator).response(decoratedResponse).build());
                }));
    }

    /**
     * 记录到请求日志中去
     *
     * @param request request
     * @param body    请求的body内容
     * @param simpleLog 是否精简打印日志
     */
    private void recordLog(ServerHttpRequest request, Object body, boolean simpleLog) {
        if (simpleLog) {
            logger.info("请求信息 - url:" + request.getURI() + ", method:" + request.getMethod() + ", body:" + body);
            return;
        }

        // 记录要访问的url
        StringBuilder builder = new StringBuilder("请求url: ");
        builder.append(request.getURI().getRawPath());

        // 记录访问的方法
        HttpMethod method = request.getMethod();
        if (null != method) {
            builder.append(", method: ").append(method.name());
        }
        // 记录头部信息
        builder.append(", header { ");
        for (Map.Entry<String, List<String>> entry : request.getHeaders().entrySet()) {
            builder.append(entry.getKey()).append(":").append(StringUtils.join(entry.getValue(), ",")).append(",");
        }

        // 记录参数
        builder.append("} param {");
        if (null != method && HttpMethod.GET.matches(method.name())) {
            // 记录请求的参数信息 针对GET 请求
            MultiValueMap<String, String> queryParams = request.getQueryParams();
            for (Map.Entry<String, List<String>> entry : queryParams.entrySet()) {
                builder.append(entry.getKey()).append("=").append(StringUtils.join(entry.getValue(), ",")).append(",");
            }
            builder.append("}");
        } else {
            builder.append(request.getURI().getQuery());
            builder.append("} body: ");
            // 从body中读取参数
            builder.append(body);
        }

        logger.info(builder.toString());
    }

    private void logTime(ServerWebExchange exchange) {
        long beginTime = exchange.getAttributeOrDefault("beginTime", 0L);
        logger.info("统计耗时 - url:" + exchange.getRequest().getURI() + ", 耗时:" + (System.currentTimeMillis() - beginTime) + "ns");
    }
}
