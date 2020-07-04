package site.xiaokui.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import site.xiaokui.entity.MallUser;

/**
 * @author HK
 * @date 2020-07-04 11:11
 */
@Service
public interface UserRepository extends JpaRepository<MallUser, Long> {

    MallUser findByUsernameAndPassword(String username, String password);

}
