package rockermq;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author HK
 * @date 2021-02-22 17:15
 */
public class BaseTest {

    static int i;

    public static ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 20, 1, TimeUnit.MINUTES,
            new ArrayBlockingQueue<>(10), new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "线程组A" + i++);
        }
    });

    /**
     * 初始化一个生产者，并发送消息，然后关闭
     */
    protected static void initProducerSendMsgAndClose(String producerGroup, String topic, String tag, String msg, int repeatCount) throws MQClientException, RemotingException, InterruptedException, MQBrokerException {
        // 创建生产者对象，指明了生产者组名
        DefaultMQProducer producer = new DefaultMQProducer(producerGroup);
        // 设置服务器地址
        producer.setNamesrvAddr(RocketMqConfig.LOCALHOST);
        // 启动实例
        producer.start();

        for (int i = 0; i < repeatCount; i++) {
            // 实例化消息对象
            Message message = new Message(topic, tag, (msg + "-" + i).getBytes());
            // 发送消息
            SendResult sendResult = producer.send(message);
            System.out.printf("producerGroup(" + producerGroup + ") send %s%n", sendResult);
        }
        // 关闭生产者
        producer.shutdown();
    }

    /**
     * 初始化消费者，一直监听消息
     * @param instanceName 默认情况下不需要设置instanceName，rocketmq会使用ip@pid(pid代表jvm名字)作为唯一标示。因此，如果在一台机器上部署多个消费者，需要指定不同的instanceName
     *
     */
    protected static void initConsumer(String instanceName, String consumerGroup, String topic, MessageModel model) throws MQClientException {
        // 创建消费者对象，指明了消费者组名
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(consumerGroup);
        consumer.setInstanceName(instanceName);
        consumer.setMessageModel(model);
        // 设置服务器地址
        consumer.setNamesrvAddr(RocketMqConfig.LOCALHOST);
        // 订阅指定主题
        consumer.subscribe(topic, "*");
        // 注册消息监听事件
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                System.out.println("instanceName(" + instanceName + ") consumerGroup(" + consumerGroup + ") receive msg:" + msgs);
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        // 启动消费者
        consumer.start();
    }
}
