package site.xiaokui.config.spring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;


/**
 * Broker:它提供一种传输服务,它的角色就是维护一条从生产者到消费者的路线，保证数据能按照指定的方式进行传输,
 * Exchange：消息交换机,它指定消息按什么规则,路由到哪个队列。
 * Queue:消息的载体,每个消息都会被投到一个或多个队列。
 * Binding:绑定，它的作用就是把exchange和queue按照路由规则绑定起来.
 * Routing Key:路由关键字,exchange根据这个关键字进行消息投递。
 * vhost:虚拟主机,一个broker里可以有多个vhost，用作不同用户的权限分离。
 * Producer:消息生产者,就是投递消息的程序.
 * Consumer:消息消费者,就是接受消息的程序.
 * Channel:消息通道,在客户端的每个连接里,可建立多个channel.
 * 使用@Component以启用自动测试，具体的测试结果见下文
 *
 * @author HK
 * @date 2019-02-25 21:48
 */
@Slf4j
public class TestRabbitMQ implements ApplicationRunner {

    @Autowired
    private OneSender oneSender;

    @Autowired
    private TwoSender1 twoSender1;

    @Autowired
    private TwoSender2 twoSender2;

    @Autowired
    private TopicSender topicSender;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.debug("测试RabbitMQ配置({})", this.getClass().getCanonicalName());
        // 测试单对单
        for (int i = 0; i < 3; i++) {
            Thread.sleep(1000);
//            oneSender.send();
//            twoSender1.send();
//            twoSender2.send();
            topicSender.send();
            topicSender.send1();
            topicSender.send2();

        }
    }

    private static AtomicInteger n = new AtomicInteger(0);

    /**
     * 配置队列，一对一，测试结果为发一收一
     */
    @Bean
    public Queue oneToOneQueue() {
        return new Queue("one");
    }

    @Component
    public static class OneSender {
        @Autowired
        private AmqpTemplate rabbitTemplate;
        private int i = 0;

        public void send() {
            String msg = "one says " + (i++);
            log.debug(msg);
            this.rabbitTemplate.convertAndSend("one", msg);
        }
    }

    @Component
    @RabbitListener(queues = "one")
    public class OneReceiver {
        @RabbitHandler
        public void process(String msg) {
            log.debug("OneReceiver receive:" + msg);
        }
    }

    /**
     * 多对多，两个一直往队列放东西，两个一直从队列里面去东西，都是乱序的，先到先得
     */
    @Bean
    public Queue twoQueue() {
        return new Queue("two");
    }

    @Component
    public static class TwoSender1 {
        @Autowired
        private AmqpTemplate rabbitTemplate;

        public void send() {
            String msg = "two1 says " + n.incrementAndGet();
            log.debug(msg);
            this.rabbitTemplate.convertAndSend("two", msg);
        }
    }

    @Component
    public static class TwoSender2 {
        @Autowired
        private AmqpTemplate rabbitTemplate;

        public void send() {
            String msg = "two2 says " + n.incrementAndGet();
            log.debug(msg);
            this.rabbitTemplate.convertAndSend("two", msg);
        }
    }

    @Component
    @RabbitListener(queues = "two")
    public class TwoReceiver1 {
        @RabbitHandler
        public void process(String msg) {
            log.debug("TwoReceiver1 receive:" + msg);
        }
    }

    @Component
    @RabbitListener(queues = "two")
    public class TwoReceiver2 {
        @RabbitHandler
        public void process(String msg) {
            log.debug("TwoReceiver2 receive:" + msg);
        }
    }

    /**
     * 只有一个TopicExchange，因此根据路由key划分路由线路，测试结果如下，
     * topic.1只会走topic.messages
     * topic.message会走topic.messages和topic.message
     * topic.messages会走topic.messages
     * 如果是FanoutExchange，那么就是广播模式了，所有绑定此Exchange都会忽略路由key，而走此线路
     */
    @Configuration
    public class TopicConfig {
        private final static String MSG = "topic.message";
        private final static String MSGS = "topic.messages";

        @Bean
        public Queue queueMessage() {
            return new Queue(MSG);
        }

        @Bean
        public Queue queueMessages() {
            return new Queue(MSGS);
        }

        @Bean
        TopicExchange exchange() {
            return new TopicExchange("exchange");
        }

        @Bean
        Binding bindingExchangeMessage(Queue queueMessage, TopicExchange exchange) {
            return BindingBuilder.bind(queueMessage).to(exchange).with("topic.message");
        }

        @Bean
        Binding bindingExchangeMessages(Queue queueMessages, TopicExchange exchange) {
            return BindingBuilder.bind(queueMessages).to(exchange).with("topic.#");
        }
    }

    @Component
    public class TopicSender {
        @Autowired
        private AmqpTemplate rabbitTemplate;

        public void send() {
            String msg = "topic.1 says " + n.incrementAndGet();
            log.debug(msg);
            this.rabbitTemplate.convertAndSend("exchange", "topic.1", msg);
        }

        public void send1() {
            String msg = "topic.message says " + n.incrementAndGet();
            log.debug(msg);
            this.rabbitTemplate.convertAndSend("exchange", "topic.message", msg);
        }

        public void send2() {
            String msg = "topic.messages says " + n.incrementAndGet();
            log.debug(msg);
            this.rabbitTemplate.convertAndSend("exchange", "topic.messages", msg);
        }
    }

    @Component
    @RabbitListener(queues = "topic.message")
    public class TopicReceiver {
        @RabbitHandler
        public void process(String message) {
            log.debug("Topic.message receive:" + message);
        }
    }

    @Component
    @RabbitListener(queues = "topic.messages")
    public class TopicReceiver2 {
        @RabbitHandler
        public void process(String message) {
            log.debug("Topic.messages receive:" + message);
        }
    }

    private TestRabbitMQ() {
        if (!log.isDebugEnabled()) {
            throw new RuntimeException("这是测试配置哟，请去掉@Configuration、@Component注解");
        }
    }
}
