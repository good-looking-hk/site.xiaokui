package site.xiaokui.beetlsql;

import org.beetl.sql.core.*;
import org.beetl.sql.core.db.DBStyle;
import org.beetl.sql.core.db.MySqlStyle;
import org.beetl.sql.ext.DebugInterceptor;
import site.xiaokui.module.user.entity.SysMenu;

import java.util.List;

/**
 * @author HK
 * @date 2018-05-24 14:32
 */
public class BeetlSql {

    private static final String DRIVER = "com.mysql.jdbc.Driver";

    private static final String URL = "jdbc:mysql://localhost:3306/test_sql?characterEncoding=UTF-8";

    private static final String USER = "root";

    private static final String PASSWORD = "199710HKhk!";

    public static void main(String[] args) {
        ConnectionSource source = ConnectionSourceHelper.getSimple(DRIVER, URL, USER, PASSWORD);
        DBStyle mysql = new MySqlStyle();
        // sql语句放在classpath的/sql 目录下
        SQLLoader loader = new ClasspathLoader("/sql");
        // 数据库命名跟java命名一样，所以采用DefaultNameConversion，还有一个是UnderlinedNameConversion，下划线风格的，
        UnderlinedNameConversion nc = new UnderlinedNameConversion();
        // 最后，创建一个SQLManager,DebugInterceptor 不是必须的，但可以通过它查看sql执行情况
        SQLManager sqlManager = new SQLManager(mysql, loader, source, nc, new Interceptor[]{new DebugInterceptor()});

        List<SysMenu> list = sqlManager.select("user.select", SysMenu.class, 1);
        for (SysMenu temp : list) {
            System.out.println(temp);
        }
    }
}
