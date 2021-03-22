package db.dao;

import db.entity.UserBalance;

/**
 * @author HK
 * @date 2021-03-19 17:56
 */
public interface UserBalanceDao {

    int insert(UserBalance record);

    int insertSelective(UserBalance record);
}
