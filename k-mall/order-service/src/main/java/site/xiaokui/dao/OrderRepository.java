package site.xiaokui.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.xiaokui.entity.MallOrder;

import java.util.List;

/**
 * @author HK
 * @date 2020-07-04 11:11
 */
@Service
public interface OrderRepository extends JpaRepository<MallOrder, Long> {

    List<MallOrder> findAllByUidAndStatus(Long uid, Integer status);

    @Transactional
    @Modifying
    @Query(value = "update mall_order set status = 1 where uid = ?1 and oid = ?2", nativeQuery = true)
    int completeOrder(Long uid, Long oid);

}
