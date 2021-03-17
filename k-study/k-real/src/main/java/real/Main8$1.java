package real;

/**
 * 两个线程A, B按序输出 0-100 之间的元素，线程A只负责输出质数,线程B负责输出其他元素
 *
 * 使用两条线程，两个锁，这是个反面例子
 *
 * 原因在于 在极限情况下代码会出问题 zsLock.notify 会出现通知失败（此时zsLock尚未进入wait），从而导致死锁
 *
 * @author HK
 * @date 2021-03-01 16:14
 */
public class Main8$1 {

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
        Object zsLock = new Object(), otherLock = new Object();
        Thread zsThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (startValue < endValue) {
                    try {
                        if (isZs(++startValue)) {
                            System.out.println(Thread.currentThread().getName() + ":" + startValue);
                        } else {
                            synchronized (otherLock) {
                                otherLock.notify();
                            }
                            synchronized (zsLock) {
                                zsLock.wait();
                            }
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, "质数线程");

        Thread otherThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (startValue < endValue) {
                    try {
                        synchronized (otherLock) {
                            otherLock.wait();
                        }
                        System.out.println(Thread.currentThread().getName() + ":" + startValue);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    synchronized (zsLock) {
                        zsLock.notify();
                    }
                }
            }
        }, "非质数线程");
        zsThread.start();
        otherThread.start();
        Thread.sleep(500);
        synchronized (zsLock) {
            zsLock.notify();
        }
    }
}
