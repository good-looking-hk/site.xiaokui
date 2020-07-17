package site.xiaokui.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Snowflake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RestController;
import site.xiaokui.config.RocketMqConfig;
import site.xiaokui.dao.OrderRepository;
import site.xiaokui.entity.MallOrder;
import site.xiaokui.entity.ResultEntity;
import site.xiaokui.enums.OrderStatus;

import org.springframework.cloud.stream.messaging.Source;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author HK
 * @date 2020-07-04 14:20
 */
@RestController
public class OrderServiceImpl implements OrderService {

    private Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final RocketMqConfig.OutChannel outChannel;

    @Autowired
    public OrderServiceImpl(RocketMqConfig.OutChannel outChannel) {
        this.outChannel = outChannel;
    }

    @Value("${snowflake.workerId}")
    private Integer workerId;

    @Value("${snowflake.dataCenterId}")
    private Integer dataCenterId;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductService productService;

    /**
     * 预下单，等待用户完成支付，等待时间两分钟
     *
     * @param uid    用户编号
     * @param pid    商品编号
     * @param name   商品名称
     * @param price  价格
     * @param payMsg 支付信息
     * @param remark 订单备注
     * @return
     */
    @Override
    public ResultEntity preOrder(Long uid, Long pid, String name, BigDecimal price, String payMsg, String remark) {
        MallOrder mallOrder = new MallOrder();
        // 雪花算法生成分布式ID
        Long oid = new Snowflake(workerId, dataCenterId).nextId();

        mallOrder.setOid(oid);
        mallOrder.setUid(uid);
        mallOrder.setPid(pid);
        mallOrder.setName(name);
        mallOrder.setPrice(price);
        mallOrder.setPayMsg(payMsg);

        // 订单状态置为待支付
        mallOrder.setStatus(OrderStatus.TO_PAY.getCode());
        Date now = new Date();
        // 设置创建时间和过期时间
        mallOrder.setCreateTime(now);
        mallOrder.setExpireTime(DateUtil.offsetMinute(now, 2));
        mallOrder.setRemark(remark);
        orderRepository.save(mallOrder);
        logger.info("订单因子{}, {} 生成预支付订单: {} {} {} {} {}", workerId, dataCenterId, oid, uid, pid, name, price);
        return ResultEntity.ok().put("oid", oid);
    }

    /**
     * 用户完成支付之后的回调接口，这里的分布式事务采用RocketMQ来保证:
     * 1.更改订单状态
     * 2.减库存
     * 3.加积分
     *
     * @param oid 订单编号
     */
    @Override
    public ResultEntity completeOrder(Long oid) {
        Optional<MallOrder> optional = orderRepository.findById(oid);
        MallOrder mallOrder = optional.orElse(null);
        if (mallOrder == null) {
            logger.warn("订单不存在:" + oid);
            return ResultEntity.error("订单不存在:" + oid);
        }
        Date now = new Date();
        if (mallOrder.getExpireTime().getTime() / 1000 < now.getTime() / 1000) {
            return ResultEntity.error("订单以失效，请重新下单:" + oid);
        }
        if (mallOrder.getStatus() != OrderStatus.TO_PAY.getCode()) {
            return ResultEntity.error("非法操作,原订单状态为:" + OrderStatus.convertCode(mallOrder.getStatus()));
        }

        // 订单状态置为已支付
        mallOrder.setStatus(OrderStatus.PAID.getCode());
        mallOrder.setCompleteTime(now);

        // 如果不采用分布式事务
//        ResultEntity result = productService.sureBuy(oid, mallOrder.getUid(), mallOrder.getPid());
//        if (!result.get("code").equals(200)) {
//            return ResultEntity.ok("减库存失败，请联系客服检查！！！");
//        }
//        orderRepository.save(mallOrder);

        Message message = MessageBuilder
                .withPayload(mallOrder)
                .setHeader("oid", oid)
                .setHeader("pid", mallOrder.getPid())
                .setHeader("price", mallOrder.getPrice())
                .build();
        logger.info("发送half消息前，oid=" + oid);
        // 发送半消息
        outChannel.orderTxChannelOutput().send(message);
        logger.info("成功发送half消息后，此时消息已提交至消息队列");
        return ResultEntity.ok("订单已完成，等待发货");
    }

    @Override
    public ResultEntity deleteOrder(Long oid) {
        orderRepository.deleteById(oid);
        return ResultEntity.ok();
    }

    @Override
    public ResultEntity toPaidOrdeeList(Long uid) {
        List<MallOrder> list = orderRepository.findAllByUidAndStatus(uid, OrderStatus.TO_PAY.getCode());
        return ResultEntity.ok().put("data", list);
    }

    @Override
    public ResultEntity paidOrderList(Long uid) {
        List<MallOrder> list = orderRepository.findAllByUidAndStatus(uid, OrderStatus.PAID.getCode());
        return ResultEntity.ok().put("data", list);
    }
}
