package db.dao;

import db.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author HK
 * @date 2021-03-17 17:50
 */
public interface UserDao {

    @Select("select * from k_user order by id")
    @Results({
            @Result(property = "createTime", column = "create_time", javaType = Date.class),
            @Result(property = "modifiedTime", column = "modified_time", javaType = Date.class),
    })
    List<User> findAll();

    int deleteAll();

    int deleteByPrimaryKey(Long id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Long id);

    List<User> pageQuery(int offset, int size);

    List<Map<String, Object>> groupByAndSum(String sum, String groupBy);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);
}
