package real;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 两个线程A,B按序输出 0-100 之间的元素，线程A只负责输出质数,线程B负责输出其他元素
 *
 * 不使用等待通知机制，而是用轮询机制 + 锁机制
 *
 * @author HK
 * @date 2021-03-03 09:28
 */
public class Main8$5 {

    /// 原子类非必需
    private static AtomicInteger atomicInteger = new AtomicInteger(1);

    private static int startValue = 1, endValue = 100;

    private static boolean isZs(int value) {
        if (value == 1 || value == 2) {
            return true;
        }
        for (int i = 2; i <= value / 2; i++) {
            if (value % i == 0) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        Object lock = new Object();
        Thread a = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
//                    synchronized (lock) {
//                        if (atomicInteger.get() <= endValue && isZs(atomicInteger.get())) {
//                            System.out.println(Thread.currentThread().getName() + ":" + atomicInteger.getAndIncrement());
//                        }
//                    }
                    synchronized (lock) {
                        if (startValue <= endValue && isZs(startValue)) {
                            System.out.println(Thread.currentThread().getName() + ":" + startValue++);
                        }
                    }
                }
            }
        }, "质数线程");

        Thread b = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
//                    synchronized (lock) {
//                        if (atomicInteger.get() <= endValue && !isZs(atomicInteger.get())) {
//                            System.out.println(Thread.currentThread().getName() + ":" + atomicInteger.getAndIncrement());
//                        }
//                    }
                    synchronized (lock) {
                        if (startValue <= endValue && !isZs(startValue)) {
                            System.out.println(Thread.currentThread().getName() + ":" + startValue++);
                        }
                    }
                }
            }
        }, "其他线程");
        a.start();
        b.start();
    }
}
