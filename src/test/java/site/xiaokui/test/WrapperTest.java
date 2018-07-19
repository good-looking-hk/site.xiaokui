package site.xiaokui.test;

import org.beetl.sql.core.SQLManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import site.xiaokui.module.sys.user.entity.SysUser;
import site.xiaokui.module.sys.user.entity.wrapper.SysUserWrapper;

import java.util.List;

/**
 * @author HK
 * @date 2018-06-09 23:45
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class WrapperTest {

    @Test
    public void test() {
        SQLManager sqlManager = SqlManager.SQL_MANAGER;
        List<SysUser> list = sqlManager.all(SysUser.class);
        SysUser user = list.get(0);
        System.out.println(user);
        SysUserWrapper userWrapper = new SysUserWrapper(user);
        System.out.println(userWrapper);
    }
}
