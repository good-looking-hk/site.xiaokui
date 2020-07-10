package test.beetlsql;

import org.beetl.sql.core.SQLManager;
import site.xiaokui.module.user.entity.SysUser;
import site.xiaokui.module.user.entity.wrapper.SysUserWrapper;

import java.util.List;

/**
 * @author HK
 * @date 2018-06-20 21:20
 */
public class UserWrapperTest {

    static SQLManager sqlManager = Sql.getSqlManager();

    public static void main(String[] args) {
        selectAll();
    }

    private static void selectAll() {
        List<SysUser> list = sqlManager.all(SysUser.class);
        Sql.print(list);

        SysUserWrapper wrapper = new SysUserWrapper(list);
        System.out.println(wrapper);
    }
}
