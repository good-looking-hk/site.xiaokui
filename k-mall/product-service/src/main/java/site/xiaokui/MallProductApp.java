package site.xiaokui;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * @author HK
 * @date 2020-07-02 13:34
 */
@SpringBootApplication
@EnableEurekaClient
public class MallProductApp {

    public static void main(String[] args) {
        SpringApplication.run(MallProductApp.class, args);
    }

}