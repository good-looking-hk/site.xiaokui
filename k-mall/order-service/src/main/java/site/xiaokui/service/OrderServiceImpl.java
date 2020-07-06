package site.xiaokui.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import site.xiaokui.dao.OrderRepository;
import site.xiaokui.entity.MallOrder;
import site.xiaokui.entity.ResultEntity;
import site.xiaokui.enums.OrderStatus;

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

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductService productService;

    @Override
    public ResultEntity preOrder(Long oid, Long uid, Long pid, String name, BigDecimal price, Integer status, String payMsg) {
        MallOrder mallOrder = new MallOrder();
        mallOrder.setOid(oid);
        mallOrder.setUid(uid);
        mallOrder.setPid(pid);
        mallOrder.setName(name);
        mallOrder.setPrice(price);
        mallOrder.setStatus(status);
        mallOrder.setPayMsg(payMsg);
        mallOrder.setCreateTime(new Date());
        mallOrder.setRemark("");
        orderRepository.save(mallOrder);
        return ResultEntity.ok();
    }

    /**
     * 这里有分布式事务:
     * 1.该订单
     * 2.减库存
     * 3.加积分
     * 4.等等
     * @param oid   订单编号
     */
    @Transactional
    @Override
    public ResultEntity completeOrder(Long oid) {
        Optional<MallOrder> optional = orderRepository.findById(oid);
        MallOrder mallOrder = optional.orElse(null);
        if (mallOrder == null) {
            logger.warn("订单不存在:" + oid);
            return ResultEntity.error("订单不存在:" + oid);
        }
        if (mallOrder.getStatus() != OrderStatus.TO_PAY.getCode()) {
            return ResultEntity.error("非法操作,原订单状态为:" + OrderStatus.convertCode(mallOrder.getStatus()));
        }
        ResultEntity result = productService.sureBuy(oid, mallOrder.getUid(), mallOrder.getPid());
        // TODO 分布式事务
        if (!result.get("code").equals(200)) {
            return ResultEntity.ok("减库存失败，请联系客服检查！！！");
        }
        // 更新订单状态
        mallOrder.setStatus(OrderStatus.PAID.getCode());
        mallOrder.setUpdateTime(new Date());
        orderRepository.save(mallOrder);
        return ResultEntity.ok("订单已完成，等待发货");
    }

    @Override
    public ResultEntity deleteOrder(Long oid) {
        orderRepository.deleteById(oid);
        return ResultEntity.ok();
    }

    @Override
    public ResultEntity toPaidOrdeList(Long uid) {
        List<MallOrder> list = orderRepository.findAllByUidAndStatus(uid, OrderStatus.TO_PAY.getCode());
        return ResultEntity.ok().put("data", list);
    }

    @Override
    public ResultEntity paidOrderList(Long uid) {
        List<MallOrder> list = orderRepository.findAllByUidAndStatus(uid, OrderStatus.PAID.getCode());
        return ResultEntity.ok().put("data", list);
    }
}
