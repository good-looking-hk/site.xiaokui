package study.hk.mianshi.jmh;//import org.openjdk.jmh.annotations.*;
//import org.openjdk.jmh.runner.Runner;
//import org.openjdk.jmh.runner.RunnerException;
//import org.openjdk.jmh.runner.options.Options;
//import org.openjdk.jmh.runner.options.OptionsBuilder;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 测试公平锁与非公平锁之间的差距，为避免潜在的误差，使用JMH测试
 * JMH，即Java Microbenchmark Harness，是专门用于代码微基准测试的工具套件，相关JMH知识请读者自行查阅相关资料
 * 以下配置为：
 * -为每个方法单独开一个进行进程、一个线程进行测试、，
 * -预热两次，一次预热一秒
 * -执行方法两次，一次执行一秒
 * -测试模式为平均耗时
 * -统计结果单位为毫秒
 * @author HK
 * @date 2020-05-27 20:47
 */
@Fork(1)
@Threads(1)
@Warmup(iterations = 2, time = 1)
@Measurement(iterations = 2, time = 1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class LockTest {

    @Benchmark
    public static void testFairLock() throws InterruptedException {
        ReentrantLock unfairLock = new ReentrantLock(false);
        runTask(unfairLock);
    }

    @Benchmark
    public static void testUnfairLock() throws InterruptedException {
        ReentrantLock fairLock = new ReentrantLock(true);
        runTask(fairLock);
    }

    private static void runTask(ReentrantLock reentrantLock) throws InterruptedException {
        int maxThread = 10000;
        ThreadPoolExecutor executor = new ThreadPoolExecutor(20, maxThread, 10, TimeUnit.SECONDS, new ArrayBlockingQueue<>(200));
        CountDownLatch countDownLatch = new CountDownLatch(maxThread);
        AtomicInteger atomicInteger = new AtomicInteger(0);
        executor.prestartAllCoreThreads();
        for (int i = 0; i < maxThread; i++) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    reentrantLock.lock();
                    try {
                        atomicInteger.addAndGet(19);
                    } finally {
                        countDownLatch.countDown();
                        reentrantLock.unlock();
                    }
                }
            });
        }
        countDownLatch.await();
        executor.shutdown();
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder().include(LockTest.class.getSimpleName()).build();
        new Runner(opt).run();
    }
}
