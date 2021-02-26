package spi;

import java.sql.*;

/**
 * @author HK
 * @date 2021-02-10 16:36
 */
public class OldSpiTest {

    static {
        try {
            // 类路径下必须存在这个类
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("找不到数据库驱动: com.mysql.cj.jdbc.Driver");
        }
    }

    public static void main(String[] args) throws SQLException {
        System.out.println("OldSpiTest 测试");
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
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/newxiaokui", "root", "199710");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

}
