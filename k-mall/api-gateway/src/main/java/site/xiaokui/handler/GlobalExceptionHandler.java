package site.xiaokui.handler;

import cn.hutool.core.date.DateUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.SimpleFormatter;


/**
 * 覆盖默认的异常处理 {@link org.springframework.boot.autoconfigure.web.reactive.error.DefaultErrorWebExceptionHandler}
 * 参考配置类 {@link org.springframework.boot.autoconfigure.web.reactive.error.ErrorWebFluxAutoConfiguration}
 * <p>
 * 全局异常处理，去除了html，只保留json
 *
 * @author hk
 */
@Order(-1)
@Configuration
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    private Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();

        if (response.isCommitted()) {
            return Mono.error(ex);
        }

        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        if (ex instanceof ResponseStatusException) {
            response.setStatusCode(((ResponseStatusException) ex).getStatus());
        }
        logger.error("网关错误，访问地址" + exchange.getRequest().getURI() + "，错误信息:" + ex.getMessage());
        return response.writeWith(Mono.fromSupplier(() -> {
            DataBufferFactory bufferFactory = response.bufferFactory();
            try {
                Map<String, Object> map = new HashMap<>(4);
                if (ex instanceof ResponseStatusException) {
                    ResponseStatusException rse = (ResponseStatusException) ex;
                    map.put("code", rse.getStatus().value());
                    if (rse.getStatus().is5xxServerError()) {
                        logger.error("服务器内部错误", ex);
                    }
                } else {
                    map.put("code", HttpStatus.INTERNAL_SERVER_ERROR.value());
                }
                map.put("msg", ex.getMessage());
                map.put("datetime", DateUtil.format(new Date(), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")));
                return bufferFactory.wrap(objectMapper.writeValueAsBytes(map));
            } catch (JsonProcessingException e) {
                logger.error("Error writing response", ex);
                return bufferFactory.wrap(new byte[0]);
            }
        }));
    }
}

