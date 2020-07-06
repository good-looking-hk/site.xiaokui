package site.xiaokui.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    ResultEntity preOrder(@RequestParam Long oid, @RequestParam Long uid, @RequestParam Long pid, @RequestParam String name, @RequestParam BigDecimal price, @RequestParam Integer status, @RequestParam String payMsg);

    /**
     * 生成支付订单
     */
    @RequestMapping("/completeOrder")
    @ResponseBody
    ResultEntity completeOrder(@RequestParam Long oid);

    /**
     * 删除订单，物理删除
     */
    @RequestMapping("/deleteOrder")
    @ResponseBody
    ResultEntity deleteOrder(Long oid);

    /**
     * 查询待支付订单
     */
    @RequestMapping("/toPaidOrder")
    @ResponseBody
    ResultEntity toPaidOrdeList(Long oid);

    /**
     * 查询已完成订单
     */
    @RequestMapping("/paidOrder")
    @ResponseBody
    ResultEntity paidOrderList(Long oid);
}
