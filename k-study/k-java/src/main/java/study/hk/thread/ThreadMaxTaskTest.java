//package study.hk.thread;
//
//import lombok.extern.slf4j.Slf4j;
//
//import java.util.concurrent.ArrayBlockingQueue;
//import java.util.concurrent.ThreadFactory;
//import java.util.concurrent.ThreadPoolExecutor;
//import java.util.concurrent.TimeUnit;
//import java.util.concurrent.atomic.AtomicInteger;
//
///**
// * @author HK
// * @date 2020-10-23 13:32
// */
//@Slf4j
//public class ThreadMaxTaskTest {
//
//    private static AtomicInteger atomicInteger = new AtomicInteger(0);
//
//    /**
//     * 让任务尽量得到充分运行
//     */
//    private static ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 15, 30, TimeUnit.SECONDS,
//            new ArrayBlockingQueue<>(200), new ThreadFactory() {
//        @Override
//        public Thread newThread(Runnable r) {
//            return new Thread(r, "采集线程" + atomicInteger.getAndIncrement());
//        }
//    }, new ThreadPoolExecutor.CallerRunsPolicy());
//
//    public static void main(String[] args) {
//        for (int i = 0; i < 200; i++) {
//            int finalI = i;
//            executor.execute(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        Thread.sleep(400);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    log.info(Thread.currentThread().getName() + ":" + finalI);
//                }
//            });
//        }
//    }
//}
