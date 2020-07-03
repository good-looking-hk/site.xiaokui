package site.xiaokui.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import site.xiaokui.entity.ResultEntity;

/**
 * @author HK
 * @date 2020-06-29 16:06
 */
@FeignClient(value = "order-service")
public interface OrderService {

    /**
     * 生成预订单
     */
    @RequestMapping("/preOrder")
    @ResponseBody
    ResultEntity preOrder(Long uid, Double price, String payMsg);

    /**
     * 生成支付订单
     */
    @RequestMapping("/createOrder")
    @ResponseBody
    ResultEntity createOrder(Long uid, Double price, String payMsg);

    /**
     * 查询订单
     */
    @RequestMapping("/list")
    @ResponseBody
    ResultEntity list(Long uid, Double price, String payMsg);
}
