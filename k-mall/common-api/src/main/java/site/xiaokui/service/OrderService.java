package site.xiaokui.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import site.xiaokui.entity.ResultEntity;

import java.math.BigDecimal;

/**
 * @author HK
 * @date 2020-06-29 16:06
 */
@FeignClient(value = "order-service")
public interface OrderService {

    /**
     * 生成预订单
     */
    @RequestMapping(value = "/preOrder")
    @ResponseBody
    ResultEntity preOrder(Long ord, Long uid, Long pid, BigDecimal price, Integer status, String payMsg);

    /**
     * 生成支付订单
     */
    @RequestMapping("/createOrder")
    @ResponseBody
    ResultEntity createOrder(Long uid, Long oid);

    /**
     * 查询待支付订单
     */
    @RequestMapping("/toPaidOrder")
    @ResponseBody
    ResultEntity toPaidOrder(Long uid);

    /**
     * 查询已完成订单
     */
    @RequestMapping("/paidOrder")
    @ResponseBody
    ResultEntity paidOrder(Long uid);
}
