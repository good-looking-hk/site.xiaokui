package real;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 两个线程A,B按序输出 0-100 之间的元素，线程A只负责输出质数,线程B负责输出其他元素
 *
 * 使用volatile，完美解决
 *
 * @author HK
 * @date 2021-03-02 14:44
 */
public class Main8$4 {

    static int startValue = 0, endValue = 100;

    static volatile boolean printZs = true;

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

    public static void main(String[] args) throws InterruptedException {

        Thread a = new Thread(new Runnable() {
            @Override
            public void run() {
                while (startValue <= endValue) {
                    if (printZs) {
                        System.out.println(Thread.currentThread().getName() + ":" + startValue);
                        printZs = isZs(++startValue);
                    }
                }
            }
        }, "质数线程");

        Thread b = new Thread(new Runnable() {
            @Override
            public void run() {
                while (startValue <= endValue) {
                    if (!printZs) {
                        System.out.println(Thread.currentThread().getName() + ":" + startValue);
                        printZs = isZs(++startValue);
                    }
                }
            }
        }, "非质数线程");
        b.start();
        a.start();
    }
}
