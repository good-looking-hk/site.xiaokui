package db;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * -XX:+PrintGCDetails，打印GC信息，这是-verbosegc默认开启的选项
 * -XX:+PrintGCTimeStamps，打印每次GC的时间戳
 * -XX:+PrintHeapAtGC：每次GC时，打印堆信息
 * -XX:+PrintGCDateStamps (from JDK 6 update 4) ：打印GC日期，适合于长期运行的服务器
 * -Xloggc:/home/admin/logs/gc.log：制定打印信息的记录的日志位置
 *
 * 对于excel的参数，有如下jvm参数设置Java堆大小为1G -Xms1024m -Xmx1024m
 * @author HK
 * @date 2017/06/25 20:22
 */
@SpringBootApplication
@MapperScan("db.dao")
public class AppRunner {

    public static void main(String[] args) {
        SpringApplication.run(AppRunner.class, args);
    }
}
