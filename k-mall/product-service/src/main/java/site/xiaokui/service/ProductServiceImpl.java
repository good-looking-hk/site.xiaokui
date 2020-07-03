package site.xiaokui.service;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import org.springframework.web.bind.annotation.RestController;
import site.xiaokui.entity.Product;
import site.xiaokui.entity.ResultEntity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author HK
 * @date 2020-07-02 13:44
 */
@RestController
public class ProductServiceImpl implements ProductService {

    @Override
    public ResultEntity all() {
        Product p1 = new Product();
        p1.setUid(1L);
        p1.setName("小米手机");
        p1.setPrice(new BigDecimal("3999"));
        p1.setStock(1000L);
        p1.setCreateTime(new Date());
        p1.setRemark("小米手机，发烧友必备！");

        Product p2 = new Product();
        p2.setUid(2L);
        p2.setName("小米笔记本");
        p2.setPrice(new BigDecimal("6999"));
        p2.setStock(500L);
        p2.setCreateTime(new Date());
        p2.setRemark("小米笔记本，轻薄版，伴你度过美好青春！");
        return ResultEntity.ok().put("data", new Product[]{p1, p2});
    }

    @Override
    public ResultEntity details(Long pid) {
        return null;
    }

    @Override
    public ResultEntity preBuy(Long pid) {
        return null;
    }

    @Override
    public ResultEntity sureBuy(Long pid) {
        return null;
    }
}
