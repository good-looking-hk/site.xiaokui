package test.beetlsql;

import org.beetl.sql.core.SQLManager;
import site.xiaokui.blog.entity.SysUser;

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
    }
}
