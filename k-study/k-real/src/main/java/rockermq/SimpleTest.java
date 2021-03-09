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
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 简单测试rocketmq是否正常启动
 * @author HK
 * @date 2021-02-01 15:20
 */
public class SimpleTest {

    static int i;

    public static ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 20, 1, TimeUnit.MINUTES,
            new ArrayBlockingQueue<>(10), new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "线程组A" + i++);
        }
    });

    private static void initProducer() throws MQClientException, RemotingException, InterruptedException, MQBrokerException {
        // 创建生产者对象，指明了生产者组名
        DefaultMQProducer producer = new DefaultMQProducer("simple");
        // 设置服务器地址
        producer.setNamesrvAddr(RocketMqConfig.LOCALHOST);
        // 启动实例
        producer.start();

        for (int i = 0; i < 3; i++) {
            String str = "Hello RocketMQ";
            // 实例化消息对象
            Message message = new Message("TopicTest1", "TagA", (str + i).getBytes());
            // 发送消息
            SendResult sendResult = producer.send(message);
            System.out.printf("send %s%n", sendResult);
        }
        // 关闭生产者
        producer.shutdown();
    }

    private static void initConsumer() throws MQClientException, InterruptedException {
        // 创建消费者对象，指明了消费者组名
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("simple");
        // 设置服务器地址
        consumer.setNamesrvAddr(RocketMqConfig.LOCALHOST);
        // 订阅指定主题
        consumer.subscribe("TopicTest1", "*");
        // 注册消息监听事件
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                System.out.println("receive msg:" + msgs);
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        // 启动消费者
        consumer.start();
    }

    public static void main(String[] args) throws Exception {
        executor.execute(() -> {
            try {
                initProducer();
            } catch (MQClientException | RemotingException | InterruptedException | MQBrokerException e) {
                e.printStackTrace();
            }
        });
        Thread.sleep(1000);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    initConsumer();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        Thread.sleep(1000 * 60);
        System.exit(0);
    }
}
