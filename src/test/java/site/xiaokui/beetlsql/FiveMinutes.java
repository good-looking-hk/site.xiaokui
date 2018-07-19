package site.xiaokui.beetlsql;

import org.beetl.sql.core.*;
import org.beetl.sql.core.db.DBStyle;
import org.beetl.sql.core.db.MySqlStyle;
import org.beetl.sql.core.query.Query;
import org.beetl.sql.ext.DebugInterceptor;

import java.util.List;

/**
 * @author HK
 * @date 2018-05-24 00:13
 */
public class FiveMinutes {

    private static final String DRIVER = "com.mysql.jdbc.Driver";

    private static final String URL = "jdbc:mysql://localhost:3306/test_sql?characterEncoding=UTF-8";

    private static final String USER = "root";

    private static final String PASSWORD = "199710HKhk!";

    public static void main(String[] args) {
        ConnectionSource source = ConnectionSourceHelper.getSimple(DRIVER, URL, USER, PASSWORD);
        DBStyle mysql = new MySqlStyle();
// sql语句放在classpagth的/sql 目录下
        SQLLoader loader = new ClasspathLoader("/sql");
// 数据库命名跟java命名一样，所以采用DefaultNameConversion，还有一个是UnderlinedNameConversion，下划线风格的，
        UnderlinedNameConversion nc = new UnderlinedNameConversion();
// 最后，创建一个SQLManager,DebugInterceptor 不是必须的，但可以通过它查看sql执行情况
        SQLManager sqlManager = new SQLManager(mysql, loader, source, nc, new Interceptor[]{new DebugInterceptor()});


//使用内置的生成的sql 新增用户，如果需要获取主键，可以传入KeyHolder
        User user = new User();
        int result = 0;
//        user.setAge(19);
//        user.setName("xiandafu");
//        int result = sqlManager.insert(user);
//        System.out.println("插入结果：" + result);

//使用内置sql查询用户
        int id = 4;
        user = sqlManager.unique(User.class, id);
        System.out.println(user);

//模板更新,仅仅根据id更新值不为null的列
        User newUser = new User();
        newUser.setId(1);
        newUser.setAge(20);
        sqlManager.updateTemplateById(newUser);
        System.out.println("更新结果：" + result);

//模板查询
        User query = new User();
        query.setName("xiandafu");
        List<User> list = sqlManager.template(query);
        System.out.println(list.size());

//Query查询
//        Query userQuery = sqlManager.query(User.class);
//        List<User> users = userQuery.lambda().andEq(User::getName, "xiandafy").select();

//使用user.md 文件里的select语句，参考下一节。
        User query2 = new User();
        query.setName("xiandafu");
        List<User> list2 = sqlManager.select("user.select", User.class, query2);
        System.out.println(list2.size());

// 这一部分需要参考mapper一章
        UserDao dao = sqlManager.getMapper(UserDao.class);
        List<User> list1 = dao.all();
        System.out.println(list1.size());
    }
}
