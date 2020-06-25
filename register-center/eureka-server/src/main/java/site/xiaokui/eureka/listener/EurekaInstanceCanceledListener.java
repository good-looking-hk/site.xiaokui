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
import site.xiaokui.base.service.EmailService;

/**
 * 用于监听eureka服务停机通知
 * @author hk
 */
@Slf4j
@Configuration
public class EurekaInstanceCanceledListener implements ApplicationListener {

    @Override
    public void onApplicationEvent(@NonNull ApplicationEvent applicationEvent) {
        // EurekaInstanceRenewedEvent{appName='EUREKA-SERVER3', serverId='192.168.1.215:eureka-server3:7003',
        // instanceInfo=InstanceInfo [instanceId = 192.168.1.215:eureka-server3:7003, appName = EUREKA-SERVER3,
        // hostName = 192.168.1.215, status = UP, ipAddr = 192.168.1.215, port = 7003, securePort = 443,
        // dataCenterInfo = com.netflix.appinfo.MyDataCenterInfo@e1b1c6a, replication=true},
        // class org.springframework.cloud.netflix.eureka.server.event.EurekaInstanceRenewedEvent
        log.info("{}, {}", applicationEvent, applicationEvent.getClass());
        // 下线/挂掉事件
        if (applicationEvent instanceof EurekaInstanceCanceledEvent) {
            EurekaInstanceCanceledEvent event = (EurekaInstanceCanceledEvent) applicationEvent;
            // 获取当前Eureka实例中的节点信息
            PeerAwareInstanceRegistry registry = EurekaServerContextHolder.getInstance().getServerContext().getRegistry();
            Applications applications = registry.getApplications();
            // 遍历获取已注册节点中与当前失效节点ID一致的节点信息
            applications.getRegisteredApplications().forEach((registeredApplication) -> {
                registeredApplication.getInstances().forEach((instance) -> {
                    if (instance.getInstanceId().equals(event.getServerId())) {
                        log.error("服务：" + instance.getAppName() + " 挂啦");
                        // 发送邮件
                        // EmailService.sendInfo("服务：" + instance.getAppName() + " 挂啦");
                        applications.removeApplication(registeredApplication);
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

