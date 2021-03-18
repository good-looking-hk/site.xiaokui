package db.dao;

import db.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author HK
 * @date 2021-03-17 17:50
 */
@Repository
@Mapper
public interface UserDao {

    @Select("select * from k_user")
    List<User> findAll();

}
