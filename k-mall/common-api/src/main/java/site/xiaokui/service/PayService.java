package site.xiaokui.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import site.xiaokui.entity.ResultEntity;

/**
 * @author HK
 * @date 2020-07-02 14:09
 */
@FeignClient(value = "pay-service")
public interface PayService {

    /**
     * 支付
     */
    @RequestMapping("/pay")
    @ResponseBody
    ResultEntity pay(@RequestParam Long uid, Double price, String payMsg);

    /**
     * 查询支付订单
     */
    @RequestMapping("/list")
    @ResponseBody
    ResultEntity list(@RequestParam Long uid);
}
