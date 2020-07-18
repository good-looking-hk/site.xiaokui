package site.xiaokui.common.hk.thread.basic;

import java.util.concurrent.TimeUnit;

/**
 * TODO
 * 有时间去研究一下jdk源码，暂时放下菜刀
 * @author HK
 * @date 2018-07-22 14:49
 */
public class WaitNotify {

    static boolean flag = true;

    static Object lock = new Object();

    public static void main(String[] args) throws InterruptedException {
        Thread waitThread = new Thread(new Wait(), "WaitThread");
        waitThread.start();
        TimeUnit.SECONDS.sleep(1);
        Thread notifyThread = new Thread(new Notify(), "NotifyThread");
        notifyThread.start();
    }

    static class Wait implements Runnable {
        @Override
        public void run() {
            synchronized (lock) {
                while (flag) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("-run- " + System.currentTimeMillis());
                sleepSecond(1);
            }
        }
    }

    static class Notify implements Runnable {
        @Override
        public void run() {
            synchronized (lock) {
                System.out.println("notify " + System.currentTimeMillis());
                lock.notifyAll();
                flag = false;
                // 如果这里不加一定的睡眠时间，那么大部分是again在前，run在后，少数相反。加了之后，几乎都是again在前，run在后，我没见过例外
//                try {
//                    TimeUnit.MILLISECONDS.sleep(10);
//                } catch (InterruptedException e) {
//                }
            }
            synchronized (lock) {
                System.out.println("again " + System.currentTimeMillis());
                sleepSecond(1);
            }
        }
    }

    public static final void sleepSecond(long seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
