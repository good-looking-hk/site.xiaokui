package site.xiaokui;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * @author HK
 * @date 2020-07-04 14:01
 */
@SpringBootApplication
@EnableEurekaClient
public class MallOrderApp {

    public static void main(String[] args) {
        SpringApplication.run(MallOrderApp.class, args);
    }
}
