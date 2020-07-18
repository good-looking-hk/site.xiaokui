package site.xiaokui.blog.sys.seckill;

import org.springframework.beans.factory.annotation.Autowired;
import site.xiaokui.module.sys.seckill.dto.SeckillResult;
import site.xiaokui.module.sys.seckill.entity.SeckillStatusEnum;
import site.xiaokui.module.sys.seckill.service.SeckillService;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author HK
 * @date 2019-01-26 14:55
 */
public class SeckillTests {

    private volatile boolean begin = false;

    private Thread[] threads = new Thread[200];

    private int i = 0;

    private AtomicInteger count = new AtomicInteger(0);

    @Autowired
    private SeckillService seckillService;

    public void init() {
        seckillService.resetDate();
    }

    public void testDbProcedure() {
        long startTime = System.currentTimeMillis();
        for (Thread t : threads) {
            t = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (!begin){
                    }
                    SeckillResult e = seckillService.executeSeckillProcedure(1, seckillService.getMD5(1, Thread.currentThread().getName()), Thread.currentThread().getName() + "");
                    if (e.getStatus() != SeckillStatusEnum.END.getCode()) {
                        System.out.println("线程" + Thread.currentThread().getName() + ":" + e.getStatusInfo());
                    }
                    count.getAndIncrement();
                }
            }, i + "");
            i++;
            t.start();
        }
        begin = true;
        while (count.get() != threads.length) {
        }
        // i没有被多线程污染
        // 不能为volatile修饰，虽然volatile有禁止重排序的功能，但那是针对表面的局部代码，并不能针对虚拟机内部代码
        System.out.println("i = " + i + ",count = " + count);
        System.out.println("开" + threads.length + "个线程执行秒杀存储过程，总耗时" + (System.currentTimeMillis() - startTime) + "ms");
    }

    public void testSpringTransactional() {
        long startTime = System.currentTimeMillis();
        ThreadPoolExecutor executor = new ThreadPoolExecutor(20, 200, 3, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(10), new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "线程" + i++);
            }
        }, new ThreadPoolExecutor.DiscardPolicy());
        // Running, pool size = 0, active threads = 0, queued tasks = 0, completed tasks = 0
        System.out.println(executor);
        for (int j = 0; j < threads.length; j++) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    while (!begin){
                    }
                    try {
                        SeckillResult e = seckillService.executeSeckillTransaction(1, seckillService.getMD5(1, Thread.currentThread().getName()), Thread.currentThread().getName() + "");
                        if (e.getStatus() != SeckillStatusEnum.END.getCode()) {
                            System.out.println(Thread.currentThread().getName() + ":" + e.getStatusInfo());
                        }
                    } catch (RuntimeException e) {
                        System.out.println(Thread.currentThread().getName() + ":" + e.getMessage());
                    }
                    count.getAndIncrement();
                }
            });
        }
        // Running, pool size = 190, active threads = 190, queued tasks = 10, completed tasks = 0
        System.out.println(executor);
        begin = true;
        while (count.get() != threads.length) {
        }
        // 190
        System.out.println("largestPoolSize:" + executor.getLargestPoolSize());
        // Running, pool size = 190, active threads = 0, queued tasks = 0, completed tasks = 200
        System.out.println(executor);
        // 有200个任务，但是只有190个线程，因此会存在10个线程出现重复秒杀的情况
        System.out.println("开" + threads.length + "个线程任务执行Spring秒杀事务，总耗时" + (System.currentTimeMillis() - startTime) + "ms");
    }
}
