package site.xiaokui.common.hk.thread;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author HK
 * @date 2019-10-08 15:37
 */
public class Join {

    private static AtomicInteger i = new AtomicInteger(0);

    private static Thread a = null, b = null, c = null;

    private static final int target = 7;

    public static void main(String[] args) throws InterruptedException {
        a = new Thread(new Runnable() {
            @Override
            public void run() {
                while (i.get() < target) {
                    System.out.println(Thread.currentThread() + ":" + i.getAndIncrement());
                    try {
                        b.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, "线程一");
        b = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread() + ":" + i.getAndIncrement());
                try {
                    c.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "线程二");
        c = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread() + ":" + i.getAndIncrement());
                try {
                    a.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }, "线程三");
        a.start();
    }
}
