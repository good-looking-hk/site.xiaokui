package site.xiaokui.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import site.xiaokui.dao.OrderRepository;
import site.xiaokui.entity.MallOrder;
import site.xiaokui.service.OrderServiceImpl;

import java.text.SimpleDateFormat;
import java.util.Optional;

/**
 * 订单事务监听器
 * 注意：rocketmq事务机制，只能保证本地事务（生产者）与消息队列接受消息的原子性，并不能保证第三方服务（消费者）的原子性
 * @author HK
 * @date 2020-07-12 16:51
 */
@RocketMQTransactionListener(txProducerGroup = "order-tx-produce-group", corePoolSize = 5, maximumPoolSize = 100)
public class OrderTransactionListenerImpl implements RocketMQLocalTransactionListener {

    private Logger logger = LoggerFactory.getLogger(OrderTransactionListenerImpl.class);

    @Autowired
    private OrderRepository orderRepository;

    /**
     * 成功发送half消息后的事务提交
     * 如果这一步出现异常，即没有按时提交，那么后续RocketMq会发送多次事务回查消息
     */
    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(Message msg, Object arg) {
        logger.info("确认事务阶段开始" + msg.getHeaders().get("oid"));
        // 发送half消息成功，订单数据
        String orderJson = new String(((byte[]) msg.getPayload()));
        // 使用jackjson反序列化
        ObjectMapper mapper = new ObjectMapper();
        // 这里反序列化需要对Date做特殊处理
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        MallOrder mallOrder = null;
        try {
            mallOrder = mapper.readValue(orderJson, MallOrder.class);
        } catch (JsonProcessingException e) {
            logger.error("反序列化失败，请检查！" + new String((byte[]) msg.getPayload()), e);
            throw new RuntimeException("反序列化失败：" + msg + " " + arg, e);
        }
        // 修改数据库
        orderRepository.save(mallOrder);
        logger.info("订单已完成，提交事务:" + mallOrder);
        // 提交事务消息
        return RocketMQLocalTransactionState.COMMIT;
    }

    /**
     * 事务回查，前置条件：客户端没有提交事务
     * 如果成功发送half消息后，长时间客户端没有发送提交或回滚请求，那么RocketMq会进行回查，以确定事务状态
     * 如果回查仍未得到确定的结果，那么消息会进入
     */
    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message msg) {
        logger.info("提交事务异常，回查事务阶段:" + msg.getHeaders().get("oid"));
        Long oid = (Long) msg.getHeaders().get("oid");
        if (oid == null) {
            logger.error("未知的订单编号:" + oid);
            return RocketMQLocalTransactionState.ROLLBACK;
        }
        Optional<MallOrder> optional = orderRepository.findById(oid);
        MallOrder mallOrder = optional.orElse(null);
        logger.info("回查事务结果是否成功:" + (mallOrder != null));
        return mallOrder != null ?RocketMQLocalTransactionState.COMMIT : RocketMQLocalTransactionState.ROLLBACK;
    }
}
