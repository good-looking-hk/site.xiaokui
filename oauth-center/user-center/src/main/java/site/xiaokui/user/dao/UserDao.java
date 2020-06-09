package site.xiaokui.user.dao;

import org.beetl.sql.core.annotatoin.Param;
import org.beetl.sql.core.annotatoin.SqlResource;
import org.beetl.sql.core.mapper.BaseMapper;
import site.xiaokui.module.user.entity.SysUser;


/**
 * @author HK
 * @date 2018-05-20 21:27
 */
@SqlResource("sys.user")
public interface UserDao extends BaseMapper<SysUser> {

    /**
     * 根据用户名获取用户
     * @param name 用户名称
     * @return 用户
     */
    SysUser getUserByName(@Param("name") String name);

    /**
     * 根据邮箱获取用户
     * @param email 邮箱
     * @return 用户
     */
    SysUser getUserByEmail(@Param("email") String email);

}
