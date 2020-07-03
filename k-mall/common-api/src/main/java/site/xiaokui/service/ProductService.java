package site.xiaokui.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import site.xiaokui.entity.ResultEntity;

/**
 * @author HK
 * @date 2020-06-29 16:05
 */
@FeignClient(value = "product-service")
public interface ProductService {

    /**
     * 查询所有商品
     */
    @RequestMapping("/list")
    @ResponseBody
    ResultEntity all();

    /**
     * 查询商品详细信息，如价格、库存、商家信息等
     */
    @RequestMapping("/details/{pid}")
    @ResponseBody
    ResultEntity details(Long pid);

    /**
     * 对某个商品进行预购买，此时尚未支付
     */
    @RequestMapping("/preBuy")
    @ResponseBody
    ResultEntity preBuy(Long pid);

    /**
     * 对某个商品进行预购买，此时已完成支付
     */
    @RequestMapping("/sureBuy")
    @ResponseBody
    ResultEntity sureBuy(Long pid);

}
