package spi;

import java.sql.*;

/**
 * jdbc4.0后可以不显式加载驱动，即不需要Class.forName
 * 它默认使用 ServiceLoader 去加载 META-INFO/services/java.sql.Driver，里面的内容即为 com.mysql.cj.jdbc.Driver，类似于一种约定俗称
 *
 * Dubbo为什么没有采用Java的SPI机制呢？
 * 原因主要有两个：
 * Java的SPI机制会一次性实例化扩展点所有实现，如果有扩展实现初始化很耗时，但如果没用上也加载，会很浪费资源;
 * Java的SPI机制没有IoC和AOP的支持，因此Dubbo用了自己的SPI机制：增加了对扩展点IoC和AOP的支持，一个扩展点可以直接setter注入其它扩展点。
 *
 * @author HK
 * @date 2021-02-10 16:32
 */
public class NewSpiTest {

    public static void main(String[] args) throws SQLException {
        System.out.println("NewSpiTest 测试");
        Connection connection = getConnection();
        PreparedStatement ps = connection.prepareStatement("select * from sys_user where id <= ?");
        ps.setInt(1, 5);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            int id = rs.getInt("id");
            String name = rs.getString("name");
            String email = rs.getString("email");
            System.out.println("id=" + id + ", name=" + name + ", email=" + email);
        }
    }

    public static Connection getConnection() {
        Connection conn = null;
        try {
            // ServiceLoader
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/newxiaokui", "root", "199710");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }
}
