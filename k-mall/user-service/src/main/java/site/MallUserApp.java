package site;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Spring Cloud Gateway 网关中心
 * @author HK
 * @date 2020-06-23 14:41
 */
@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
public class MallUserApp {

    public static void main(String[] args) {
        SpringApplication.run(MallUserApp.class, args);
    }
}
