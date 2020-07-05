package site.xiaokui.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import site.xiaokui.dao.OrderRepository;
import site.xiaokui.entity.MallOrder;
import site.xiaokui.entity.ResultEntity;
import site.xiaokui.enums.OrderStatus;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author HK
 * @date 2020-07-04 14:20
 */
@RestController
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public ResultEntity preOrder(Long ord, Long uid, Long pid, BigDecimal price, Integer status, String payMsg) {
        MallOrder mallOrder = new MallOrder();
        mallOrder.setOid(ord);
        mallOrder.setUid(uid);
        mallOrder.setPid(pid);
        mallOrder.setPrice(price);
        mallOrder.setStatus(OrderStatus.convertCode(status));
        mallOrder.setPayMsg(payMsg);
        orderRepository.save(mallOrder);
        return ResultEntity.ok();
    }


    @Override
    public ResultEntity createOrder(Long uid, Long oid) {
        return null;
    }

    @Override
    public ResultEntity toPaidOrder(Long uid) {
        List<MallOrder> list = orderRepository.findAllByUidAndStatus(uid, OrderStatus.TO_PAY.getCode());
        return ResultEntity.ok().put("data", list);
    }

    @Override
    public ResultEntity paidOrder(Long uid) {
        List<MallOrder> list = orderRepository.findAllByUidAndStatus(uid, OrderStatus.PAID.getCode());
        return ResultEntity.ok().put("data", list);
    }
}
