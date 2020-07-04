package site.xiaokui;

import cn.hutool.core.io.resource.StringResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.junit4.SpringRunner;
import site.xiaokui.entity.ResultEntity;
import site.xiaokui.service.ProductServiceImpl;

import javax.sql.DataSource;
import javax.validation.constraints.NotNull;
import java.sql.SQLException;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProductTests {

    static int i = 0;

    @Autowired
    private ProductServiceImpl productService;

    @Autowired
    private DataSource dataSource;

    /**
     * 初始化sql语句如下
     * INSERT INTO `mall_product`(`pid`, `name`, `price`, `stock`, `create_time`, `remark`) VALUES (3, '苹果笔记本', 16999.00, 100, '2020-03-03 03:03:03', '低调奢华有内涵！');
     * 一共有100个商品，尝试用200个线程去减库存
     */
    @Test
    public void testReduceStock() throws InterruptedException, SQLException {
        AtomicInteger str2001 = new AtomicInteger(0);
        AtomicInteger str2002 = new AtomicInteger(0);
        AtomicInteger str200 = new AtomicInteger(0);

        ThreadPoolExecutor executor = new ThreadPoolExecutor(110, 200, 2, TimeUnit.MINUTES,
                new ArrayBlockingQueue<>(100), new ThreadFactory() {
            @Override
            public Thread newThread(@NotNull Runnable r) {
                Thread thread = new Thread(r,"线程" + i++);
                thread.setDaemon(true);
                return thread;
            }
        }, new ThreadPoolExecutor.DiscardOldestPolicy());
        executor.prestartAllCoreThreads();

        // 初始化测试sql语句
        productService.delete(3L);
        String sql = "INSERT INTO `mall_product`(`pid`, `name`, `price`, `stock`, `create_time`, `remark`) VALUES (3, '苹果笔记本', 16999.00, 100, '2020-03-03 03:03:03', '低调奢华有内涵！');";
        ScriptUtils.executeSqlScript(dataSource.getConnection(), new ByteArrayResource(sql.getBytes()));
        System.out.println("测试前商品情况：" + productService.details(3L));
        // 开始测试
        for (int i = 0; i < 200; i++) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    ResultEntity result = productService.preBuy(3L);
                    if (result.get("code").equals(2001)) {
                        str2001.getAndIncrement();
                    } else if (result.get("code").equals(2002)) {
                        str2002.getAndIncrement();
                    } else if (result.get("code").equals(200)) {
                        str200.getAndIncrement();
                    } else {
                        throw new RuntimeException("未知分支");
                    }
                }
            });
        }
        // 等待线程任务执行完毕
        Thread.sleep(5000);
        executor.shutdown();
        System.out.println("成功减库存数：" + str200.get());
        System.out.println("失败减库存数：" + str2002.get());
        System.out.println("未找到产品数：" + str2001.get());
        assert  str200.get() == 100;
        assert  str2002.get() == 100;
    }
}
