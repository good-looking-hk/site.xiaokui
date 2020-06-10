package site.xiaokui.eureka.listener;


import com.netflix.discovery.shared.Applications;
import com.netflix.eureka.EurekaServerContextHolder;
import com.netflix.eureka.registry.PeerAwareInstanceRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.eureka.server.event.*;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;

/**
 * 用于监听eureka服务停机通知
 * @author hk
 */
@Slf4j
@Configuration
public class EurekaInstanceCanceledListener implements ApplicationListener {

    @Override
    public void onApplicationEvent(@NonNull ApplicationEvent applicationEvent) {
        // 下线/挂掉事件
        if (applicationEvent instanceof EurekaInstanceCanceledEvent) {
            EurekaInstanceCanceledEvent event = (EurekaInstanceCanceledEvent) applicationEvent;
            // 获取当前Eureka实例中的节点信息
            PeerAwareInstanceRegistry registry = EurekaServerContextHolder.getInstance().getServerContext().getRegistry();
            Applications applications = registry.getApplications();
            // 遍历获取已注册节点中与当前失效节点ID一致的节点信息org.springframework.lang.NonNull
            applications.getRegisteredApplications().forEach((registeredApplication) -> {
                registeredApplication.getInstances().forEach((instance) -> {
                    if (instance.getInstanceId().equals(event.getServerId())) {
                        log.error("服务：" + instance.getAppName() + " 挂啦");
                        // TODO: 扩展消息提醒 邮件、手机短信、微信等
                        // 移除实例，避免频繁的无效ping
                        registeredApplication.removeInstance(instance);

                    }
                });
            });
        }
        // 注册事件
        if (applicationEvent instanceof EurekaInstanceRegisteredEvent) {
            EurekaInstanceRegisteredEvent event = (EurekaInstanceRegisteredEvent) applicationEvent;
            log.info("服务：" + event.getInstanceInfo().getAppName() + " 注册成功");
        }
        // 心跳/续约事件
        if (applicationEvent instanceof EurekaInstanceRenewedEvent) {
            EurekaInstanceRenewedEvent event = (EurekaInstanceRenewedEvent) applicationEvent;
            // 这个频率会比较频繁，可以适当延迟以下打印
            log.info("心跳检测服务：" + event.getInstanceInfo().getAppName() + "");
        }
        // Eureka注册中心启动事件
        if (applicationEvent instanceof EurekaRegistryAvailableEvent) {
            EurekaRegistryAvailableEvent event = (EurekaRegistryAvailableEvent) applicationEvent;
            log.info("注册中心：" + event.toString() + " 启动");
        }
        if (applicationEvent instanceof EurekaServerStartedEvent) {
            EurekaServerStartedEvent event = (EurekaServerStartedEvent) applicationEvent;
            log.info("消费者：" + event.toString() + " 启动");
        }
    }
}

