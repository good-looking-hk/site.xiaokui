package real;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 两个线程A,B按序输出 0-100 之间的元素，线程A只负责输出质数,线程B负责输出其他元素
 *
 * 三线程 - Lock接口 - 极端情况下会有问题
 *
 * @author HK
 * @date 2021-03-02 14:44
 */
public class Main8$4 {

    private static Lock lock = new ReentrantLock();

    private static Condition main = lock.newCondition(), one = lock.newCondition(), two = lock.newCondition();

    static int startValue = 0, endValue = 100;

    static AtomicInteger i = new AtomicInteger(0);

    public static void main(String[] args) throws InterruptedException {

        Thread a = new Thread(new Runnable() {
            @Override
            public void run() {
                while (startValue < endValue) {
                    lock.lock();
                    try {
//                        System.out.println(i.getAndIncrement() + " one线程获得锁，准备在await上等待");
                        one.await();
//                        System.out.println(i.getAndIncrement() + " one线程从await返回，打印输出，且准备通知main线程");
                        System.out.println(Thread.currentThread().getName() + ":" + startValue);
                        main.signal();
//                        System.out.println(i.getAndIncrement() + " one线程已成功通知main线程");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
//                        System.out.println(i.getAndIncrement() + " one线程准备释放锁");
                        lock.unlock();
//                        System.out.println(i.getAndIncrement() + " one线程已释放锁");
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
                        two.await();
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
        a.start();
        Thread.sleep(500);
        while (startValue < endValue) {
            lock.lock();
            try {
//                System.out.println(i.getAndIncrement() + " main线程获得锁");
                if (++startValue % 2 != 0) {
//                    System.out.println(i.getAndIncrement() + " main线程准备通知one线程");
                    one.signal();
//                    System.out.println(i.getAndIncrement() + " main线程成功通知one线程，在main上await");
                    main.await();
//                    System.out.println(i.getAndIncrement() + " main线程从await返回");
                } else {
                    two.signal();
                    main.await();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
//                System.out.println(i.getAndIncrement() + " main线程准备释放锁");
                lock.unlock();
//                System.out.println(i.getAndIncrement() + " main已释放锁");
            }
        }
        System.exit(0);
    }
}
