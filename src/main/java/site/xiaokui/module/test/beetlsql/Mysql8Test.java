package site.xiaokui.module.test.beetlsql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * @author HK
 * @date 2018-12-08 09:44
 */
public class Mysql8Test {

    public static void main(String[] args) throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection conn = DriverManager.getConnection("jdbc:mysql://120.79.20.49/newxiaokui?useSSL=FALSE&serverTimezone=UTC","root", "1238127943Hk!");
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery("select * from sys_user");
        while (rs.next()) {
            System.out.println(rs.getString(1));
        }
    }
}
