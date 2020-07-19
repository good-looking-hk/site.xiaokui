package site.xiaokui;

import cn.hutool.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Mono;
import site.xiaokui.service.TokenService;

/**
 * Spring Cloud Gateway 网关中心
 *
 * @author HK
 * @date 2020-06-23 14:41
 */
@SpringBootApplication
@EnableEurekaClient
public class MallApiGatewayApp {

    @Autowired
    private TokenService tokenService;

    public static void main(String[] args) {
        SpringApplication.run(MallApiGatewayApp.class, args);
    }

    /**
     * 测试Spring Cloud Gateway官网的这句话（修改网关代理的请求/返回内容)，仔细想想也是对的，总不能在yml文件中写代码逻辑吧:
     * This filter can be configured only by using the Java DSL.
     *
     * 通过请求命令示例
     * curl -XPOST -d 'username=admin&password=admin' http://localhost:8000/testRoute1/login
     */
    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("testRoute", r -> r.path("/user-service/login")
                        .filters(f -> f.stripPrefix(1)
                                .modifyRequestBody(String.class, String.class,
                                        ((exchange, s) -> {
                                            System.out.println("请求url:" + exchange.getRequest().getURI() + " body内容:" + s);
                                            if (s == null) {
                                                s = "";
                                            }
                                            return Mono.just(s);
                                        }))
                                .modifyResponseBody(String.class, String.class,
                                        (exchange, s) -> {
                                            System.out.println("服务端返回:" + s);
                                            JSONObject json = new JSONObject(s);
                                            String token = json.getStr("token");
                                            String uid = json.getStr("uid");
                                            String username = json.getStr("username");
                                            tokenService.addToken(token, (uid + "#" + token + "*" + username + "^").hashCode());
                                            System.out.println("为用户" + uid + "/" + username + "发放token:" + token);
                                            return Mono.just(s);
                                        })).uri("lb://user-service"))
                .build();
    }
}
