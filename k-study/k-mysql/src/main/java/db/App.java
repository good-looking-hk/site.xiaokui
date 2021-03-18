package db;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author HK
 * @date 2017/06/25 20:22
 */
@SpringBootApplication
@MapperScan("db.dao")
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
