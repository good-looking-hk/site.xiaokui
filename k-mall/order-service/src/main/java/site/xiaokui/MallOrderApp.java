package site.xiaokui;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author HK
 * @date 2020-07-04 14:01
 */
@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
public class MallOrderApp {

    public static void main(String[] args) {
        SpringApplication.run(MallOrderApp.class, args);
    }
}
