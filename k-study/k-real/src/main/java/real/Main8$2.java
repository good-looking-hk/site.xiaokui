package real;

/**
 * 两个线程A, B按序输出 0-100 之间的元素，线程A只负责输出质数,线程B负责输出其他元素
 * <p>
 * 使用两条线程，一个锁，依赖等待-通知机制，这是个正面面例子
 *
 * 这里的关键点在于 需要确保notify会通知一条正在wait的线程成功竞争锁，因此有如下关键点
 * - 1 notify调用时，必须有一条线程正在wait
 * - 2 notify调用完成后，当前线程必须马上进入wait，防止与wait线程竞争锁（由于重入锁机制，wait线程大概率竞争不过）
 * - 3 wait线程唤醒后，必须确保wait线程notify的调用以唤醒之前唤醒wait的线程，保持循环状态
 * - 4 锁对象同一时刻应该只被一个线程拥有，因此循环条件可以在锁对象内部
 *
 * @author HK
 * @date 2021-03-02 13:36
 */
public class Main8$2 {

    static int startValue = 0, endValue = 100;

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
        Object lock = new Object();
        Thread zsThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (startValue < endValue) {
                    synchronized (lock) {
                        if (isZs(++startValue)) {
                            System.out.println(Thread.currentThread().getName() + ":" + startValue);
                        } else {
                            lock.notify();
                            try {
                                lock.wait();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }, "质数线程");

        Thread otherThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (startValue < endValue) {
                    synchronized (lock) {
                        try {
                            lock.wait();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        System.out.println(Thread.currentThread().getName() + ":" + startValue);
                        lock.notify();
                    }
                }
            }
        }, "非质数线程");
        otherThread.start();
        Thread.sleep(500);
        zsThread.start();
    }
}
