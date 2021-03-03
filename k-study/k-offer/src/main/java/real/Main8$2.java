package real;

/**
 * 两个线程A,B按序输出 0-100 之间的元素，线程A只负责输出质数,线程B负责输出其他元素 - 使用三条线程
 *
 * 在极限情况下，代码会出问题
 *
 * @author HK
 * @date 2021-03-02 13:36
 */
public class Main8$2 {

    volatile static boolean notEnd = true;

    volatile static boolean successNotify = false;

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
        Object zsLock = new Object(), otherLock = new Object(), mainLock = new Object();
        Thread zsThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (notEnd) {
                    try {
                        synchronized (zsLock) {
                            zsLock.wait();
                        }
                        System.out.println(Thread.currentThread().getName() + ":" + startValue);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    while (!successNotify) {
                    }
                    synchronized (mainLock) {
                        mainLock.notify();
                    }
                }
            }
        }, "质数线程");

        Thread otherThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (notEnd) {
                    try {
                        synchronized (otherLock) {
                            otherLock.wait();
                        }
                        System.out.println(Thread.currentThread().getName() + ":" + startValue);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    while (!successNotify) {
                    }
                    synchronized (mainLock) {
                        mainLock.notify();
                    }
                }
            }
        }, "非质数线程");
        Thread mainThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (notEnd) {
                    synchronized (mainLock) {
                        try {
                            successNotify = true;
                            mainLock.wait();
                            successNotify = false;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (startValue == endValue) {
                            System.exit(0);
                        }
                        if (isZs(++startValue)) {
                            synchronized (zsLock) {
                                zsLock.notify();
                            }
                        } else {
                            synchronized (otherLock) {
                                otherLock.notify();
                            }
                        }
                    }
                }
            }
        }, "主线程");
        zsThread.start();
        otherThread.start();
        mainThread.start();
        Thread.sleep(500);
        synchronized (mainLock) {
            mainLock.notify();
        }
    }
}
