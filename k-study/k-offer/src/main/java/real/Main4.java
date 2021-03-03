package real;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 3个线程轮流打印 1 2 3 4 5 6 7 8 9 - 完美通过
 * @author HK
 * @date 2021-03-01 09:13
 */
public class Main4 {

    static int startValue = 1, endValue = 8;

    static Lock lock = new ReentrantLock();

    static Condition aLock = lock.newCondition(), bLock = lock.newCondition(), cLock = lock.newCondition();

    public static void main(String[] args) throws Exception {
        Thread a = new Thread(new Runnable() {
            @Override
            public void run() {
                while (startValue < endValue) {
                    // 同一时间只能有一个线程拥有锁
                    lock.lock();
                    try {
                        // 使线程暂时休眠等待，隐式释放锁，等待其他线程的signal唤醒
                        cLock.await();
                        System.out.println(Thread.currentThread().getName() + ":" + startValue++);
                        aLock.signal();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        lock.unlock();
                    }
                }
            }
        }, "线程a");

        Thread b = new Thread(new Runnable() {
            @Override
            public void run() {
                while (startValue < endValue) {
                    lock.lock();
                    try {
                        aLock.await();
                        System.out.println(Thread.currentThread().getName() + ":" + startValue++);
                        bLock.signal();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        lock.unlock();
                    }
                }
            }
        }, "线程b");

        Thread c = new Thread(new Runnable() {
            @Override
            public void run() {
                while (startValue < endValue) {
                    lock.lock();
                    try {
                        bLock.await();
                        System.out.println(Thread.currentThread().getName() + ":" + startValue++);
                        cLock.signal();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        lock.unlock();
                    }
                }
            }
        }, "线程c");
        a.start();
        b.start();
        c.start();
        Thread.sleep(500);
        lock.lock();
        try {
            cLock.signal();
        } finally {
            lock.unlock();
        }
    }
}
