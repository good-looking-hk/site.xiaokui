package site.xiaokui.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * 一般300兆足够了，可选配置参数如下
 * --spring.profiles.active=master1
 * -Xms200m -Xmx400m
 * @author hk
 */
@EnableEurekaServer
@SpringBootApplication
///@EnableHystrixDashboard
///@EnableTurbine
public class EurekaServerApp {

    public static void main(String[] args) {
        /// new SpringApplicationBuilder(EurekaServerApp.class).profiles("slave0").run(args);
        SpringApplication.run(EurekaServerApp.class, args);
    }
}