//package site.xiaokui.config.zk;
//
//import org.apache.curator.framework.CuratorFramework;
//import org.apache.curator.framework.CuratorFrameworkFactory;
//import org.apache.curator.retry.ExponentialBackoffRetry;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.retry.RetryPolicy;
//
///**
// * @author HK
// * @date 2020-05-18 13:48
// */
////@Configuration
//public class ZookeeperConf {
//
//    @Value("${zk.url}")
//    private String zkUrl;
//
//    @Bean
//    public CuratorFramework getCuratorFramework() {
//        RetryPolicy retryPolicy = (RetryPolicy) new ExponentialBackoffRetry(1000,3);
//        CuratorFramework client = CuratorFrameworkFactory.newClient(zkUrl, (org.apache.curator.RetryPolicy) retryPolicy);
//        client.start();
//        return client;
//    }
//}
