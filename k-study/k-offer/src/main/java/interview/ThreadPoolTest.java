package interview;

import java.util.concurrent.*;

/**
 * @author HK
 * @date 2021-01-24 15:58
 */
public class ThreadPoolTest {

    static int i = 0;

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName() + "打印了这条信息");
            }
        };
        // 默认饱和策略就是抛错，抛RejectedExecutionException
        // 当非核心线程闲置特定时间后会被系统回收
        // 可以通过executor.allowCoreThreadTimeOut(true)来设置核心线程闲置回收
        ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 20, 1, TimeUnit.MINUTES,
                new ArrayBlockingQueue<>(10), new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "线程组A" + i++);
            }
        });
        // 执行任务
        executor.execute(runnable);
        // 活动的线程数，注意这里存在的线程并发，这里可能为0，也可能为1
        System.out.println("线程池A中活动线程数：" + executor.getActiveCount() + "，存活线程数：" + executor.getPoolSize());
        // 关闭的原理都是遍历线程池中的工作线程，然后逐个调用线程的interrupt方法来中断线程
        // 所以无法响应中断的任务可能永远无法终止
        // 等待任务执行完毕才真正关闭线程池
        executor.shutdown();

        // 最好强制指定LinkedBlockingDeque的容量
        executor = new ThreadPoolExecutor(2, 6, 4, TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(1), r -> new Thread(r,"线程组B" + i++), new ThreadPoolExecutor.DiscardPolicy());
        // 提前预热/创建所有核心线程，返回预热成功数
        int startCount = executor.prestartAllCoreThreads();
        executor.setCorePoolSize(4);
        // 注意这一步的中 executor.getActiveCount()存在的线程并发，这里的 getPoolSize值的是线程中存活的线程数，这里为2
        // 默认线程池并不会初始化线程，预热其实是一个新建线程空任务的过程
        // 所以当获取活动线程数时，可能空任务还没执行完
        // 所以如果加上这一句 Thread.sleep(1)，即使是一毫秒，也可以保证executor.getActiveCount()的结果大概率为0
        // getPoolSize的大小可以认为就是线程池内部Work的个数，闲置的Work/线程处于等待状态
        System.out.println("\n线程池B中活动线程数：" + executor.getActiveCount() + "，预热成功数：" + startCount + "，存活线程数：" + executor.getPoolSize());
        // 空任务，但这也会触发线程池中一个新线程的的诞生，因为getPoolSize=2 < setCorePoolSize=4
        executor.execute(() -> {});
        // 结果为3 4
        System.out.println("存活线程数：" + executor.getPoolSize() + "，核心线程值：" + executor.getCorePoolSize());
        // 让线程池达到最大值，这会丢弃后面的两个任务
        // 打印10
        for (int i = 0; i < 10; i ++) {
            final int temp = i;
            // 随机打印0~9中的随机7个数，即最大任务数 + 任务队列数 = 6 + 1 = 7
            executor.execute(() -> {
                try {
                    // 延长耗时
                    System.out.print(temp);
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
        // 为6 4 6
        System.out.println("\n存活线程数：" + executor.getPoolSize() + "，核心线程值：" + executor.getCorePoolSize() + "，历史最大线程值" + executor.getLargestPoolSize());
        // 让非工作线程闲置时间超过运行的最大闲置时间，从而让系统回收非核心线程
        Thread.sleep(4500);
        // 为4 4 6
        System.out.println("\n存活线程数：" + executor.getPoolSize() + "，核心线程值：" + executor.getCorePoolSize() + "，历史最大线程值" + executor.getLargestPoolSize());
        // 让核心线程也可以被系统回收
        executor.allowCoreThreadTimeOut(true);
        Thread.sleep(4500);
        // 为0 4 6
        System.out.println("\n存活线程数：" + executor.getPoolSize() + "，核心线程值：" + executor.getCorePoolSize() + "，历史最大线程值" + executor.getLargestPoolSize());
        // 不等待任务执行完毕就关闭线程池
        executor.shutdownNow();

        // 也可以使用JDK默认给的线程池，虽然不建议
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        Future future = executorService.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return "面向异步编程！";
            }
        });
        System.out.println(future.get());

        i = 0;
        // 测试，假设是无界队列（未指定大小），看是否能无限容纳对象任务
        ExecutorService executorService1 = new ThreadPoolExecutor(2, 2, 1, TimeUnit.MINUTES, new LinkedBlockingDeque<>(), new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "第二组线程" + i++);
            }
        }, new ThreadPoolExecutor.AbortPolicy());
        for (int j = 0; j < 6; j++) {
            executorService1.execute(new Runnable() {
                @Override
                public void run() {
                    System.out.println(Thread.currentThread().getName() + "打印了这条信息1111");
                    ThreadUtil.sleep(200);
                }
            });
        }
        // 上面的答案是确定的，再未明确指定 LinkedBlockingDeque 大小的时候，队列为int最大值，因此可容纳巨多个任务，从而可以造成操作系统异常
        // 测试一下，假设一个线程在执行任务过程中，抛出异常（未被正确捕捉）
        executorService1.execute(new Runnable() {
            @Override
            public void run() {
                ThreadUtil.sleep(100);
                System.out.println("======================");
                throw new IllegalArgumentException("简单测试");
            }
        });
        ThreadUtil.sleep(1000);
        System.out.println("\n存活线程数：" + executor.getPoolSize() + "，核心线程值：" + executor.getCorePoolSize() + "，历史最大线程值" + executor.getLargestPoolSize());
        for (int j = 0; j < 6; j++) {
            executorService1.execute(new Runnable() {
                @Override
                public void run() {
                    System.out.println(Thread.currentThread().getName() + "打印了这条信息2222");
                    ThreadUtil.sleep(200);
                }
            });
        }
        // 从输出结果，我们不难发现，线程池将抛出异常的线程进行销毁，并重新new出了一个新线程
        // 所以说，如果使用线程池不恰当的话，可能会丢失某些关键信息
    }
}
// 为节省版面，省略输出