package site.xiaokui.service.mq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

/**
 * @author HK
 * @date 2020-07-12 20:00
 */
@Service
public class ProductReceiver {

    private Logger logger = LoggerFactory.getLogger(ProductReceiver.class);

    @StreamListener(Sink.INPUT)
    public void receive(Message message) {
        logger.info("收到消息" + message);
        //模拟消费异常
        String consumeError = (String)message.getHeaders().get("consumeError");
        if ("1".equals(consumeError)) {
            System.err.println("============Exception：积分进程挂了，消费消息失败");
            //模拟插入订单后服务器挂了，没有commit事务消息
            throw new RuntimeException("积分服务器挂了");
        }

        System.out.println("============收到订单信息，增加积分:" + message);
    }

    /**
     * 消费死信队列
     */
    @StreamListener("inputDlq")
    public void receiveDlq(Message message) {
        logger.warn("收到消息" + message);
        String orderId = (String)message.getHeaders().get("orderId");
        System.err.println("============消费死信队列消息，记录日志并预警：" + orderId);
    }
}
