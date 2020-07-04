package site.xiaokui.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.xiaokui.entity.MallProduct;


/**
 * @author HK
 * @date 2020-07-04 11:11
 */
@Service
public interface ProductRepository extends JpaRepository<MallProduct, Long> {

    /**
     * 预订单中，减库存操作
     * @param pid 商品id
     * @param number    商品数量
     * @return 返回影响行
     */
    @Transactional
    @Modifying
    @Query(value = "update mall_product set stock = stock - ?2 where pid = ?1 and stock - ?2 >= 0", nativeQuery = true)
    int reduceStock(Long pid, Integer number);

    /**
     * 预订单中，超时未支付，回库存操作
     * @param pid 商品id
     * @param number    商品数量
     * @return 返回影响行
     */
    @Transactional
    @Modifying
    @Query(value = "update mall_product set stock = stock + ?2 where pid = ?1", nativeQuery = true)
    int rollbackStock(Long pid, Integer number);
}
