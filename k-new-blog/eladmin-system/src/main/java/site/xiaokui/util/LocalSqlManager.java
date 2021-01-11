package site.xiaokui.util;

import org.beetl.sql.core.*;
import org.beetl.sql.core.db.DBStyle;
import org.beetl.sql.core.db.MySqlStyle;
import org.beetl.sql.ext.DebugInterceptor;

import java.util.List;

/**
 * @author HK
 * @date 2018-06-09 23:45
 */
public class LocalSqlManager {

    private static final String DRIVER = "com.mysql.jdbc.Driver";

    private static final String URL = "jdbc:mysql://localhost:3306/eladmin?serverTimezone=Asia/Shanghai&characterEncoding=utf8&useSSL=false";

    private static final String USER = "root";

    private static final String PASSWORD = "199710";

    public static SQLManager getSqlManager() {
        return initSqlManager();
    }

    private static SQLManager initSqlManager() {
        ConnectionSource source = ConnectionSourceHelper.getSimple(DRIVER, URL, USER, PASSWORD);
        DBStyle mysql = new MySqlStyle();
        // sql语句放在classpagth的/sql 目录下
        SQLLoader loader = new ClasspathLoader("/sql");
        // 数据库命名跟java命名一样，所以采用DefaultNameConversion，还有一个是UnderlinedNameConversion，下划线风格的，
        UnderlinedNameConversion nc = new UnderlinedNameConversion();
        // 最后，创建一个SQLManager,DebugInterceptor 不是必须的，但可以通过它查看sql执行情况
        return new SQLManager(mysql, loader, source, nc, new Interceptor[]{new DebugInterceptor()});
    }
}
