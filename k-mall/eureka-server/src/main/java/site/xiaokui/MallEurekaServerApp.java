package site.xiaokui;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * --spring.profiles.active=master
 * 一般300兆足够了，可选配置参数如下
 * -Xms200m -Xmx400m
 * @author hk
 */
@EnableEurekaServer
@SpringBootApplication
public class MallEurekaServerApp {

    public static void main(String[] args) {
        SpringApplication.run(MallEurekaServerApp.class, args);
    }
}