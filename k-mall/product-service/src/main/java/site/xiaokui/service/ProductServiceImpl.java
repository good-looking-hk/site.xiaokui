package site.xiaokui.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import site.xiaokui.dao.ProductRepository;
import site.xiaokui.entity.MallProduct;
import site.xiaokui.entity.ResultEntity;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

/**
 * @author HK
 * @date 2020-07-02 13:44
 */
@RestController
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public ResultEntity all() {
        List<MallProduct> list = productRepository.findAll();
        return ResultEntity.ok().put("data", list);
    }

    @Override
    public ResultEntity details(@NotNull Long pid) {
        Optional<MallProduct> optional = productRepository.findById(pid);
        MallProduct product = optional.orElse(null);
        if (product == null) {
            return new ResultEntity(2001, "商品不存在");
        }
        return ResultEntity.ok().put("data", product);
    }

    /**
     * 既然是预下单，那么就要实现锁库存，当特定时间内未完成付款，商品需要回仓
     * 暂时将商品购买数量限制为1
     */
    @Override
    public ResultEntity preBuy(@NotNull Long pid) {
        Optional<MallProduct> optional = productRepository.findById(pid);
        MallProduct product = optional.orElse(null);
        if (product == null) {
            return new ResultEntity(2001, "商品不存在");
        }
        if (product.getStock() <= 0) {
            return new ResultEntity(2002, "该商品已卖完");
        }
        // 商品存在，暂时将商品库存减1
        int affectRow = productRepository.reduceStock(pid, 1);
        // 减库存失败
        if (affectRow == 0) {
            return new ResultEntity(2002, "该商品已卖完");
        }
        // 向支付中心提交订单
        return ResultEntity.ok();
    }

    public ResultEntity delete(@NotNull Long pid) {
        productRepository.deleteById(pid);
        return ResultEntity.ok();
    }

    @Override
    public ResultEntity sureBuy(@NotNull Long pid) {
        return null;
    }
}
