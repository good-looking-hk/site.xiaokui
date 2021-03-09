package real;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 两个线程A,B按序输出 0-100 之间的元素，线程A只负责输出质数,线程B负责输出其他元素
 *
 * 两线程 - Lock接口
 *
 * @author HK
 * @date 2021-03-02 14:20
 */
public class Main8$3 {

    private static Lock lock = new ReentrantLock();

    private static Condition main = lock.newCondition(), other = lock.newCondition();

    static int startValue = 0, endValue = 100;

    public static void main(String[] args) throws InterruptedException {

        Thread a = new Thread(new Runnable() {
            @Override
            public void run() {
                while (startValue < endValue) {
                    if (++startValue % 2 != 0) {
                        System.out.println(Thread.currentThread().getName() + ":" + startValue);
                    } else {
                        lock.lock();
                        try {
                            other.signal();
                            main.await();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } finally {
                            lock.unlock();
                        }
                    }
                }
            }
        }, "奇数线程");

        Thread b = new Thread(new Runnable() {
            @Override
            public void run() {
                while (startValue < endValue) {
                    lock.lock();
                    try {
                        other.await();
                        System.out.println(Thread.currentThread().getName() + ":" + startValue);
                        main.signal();
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        lock.unlock();
                    }
                }
            }
        }, "偶数线程");
        b.start();
        Thread.sleep(500);
        a.start();
    }
}
