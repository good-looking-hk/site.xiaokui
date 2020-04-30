package site.xiaokui.common.hk.thread;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author HK
 * @date 2019-10-08 11:24
 */
public class Wait {

    private static AtomicInteger i = new AtomicInteger(0);

    private static final int target = 7;

    public static void main(String[] args) throws InterruptedException {
        Object lockAB = new Object();
        Object lockBC = new Object();
        Object lockCA = new Object();
        Thread a = new Thread(new Runnable() {
            @Override
            public void run() {
                while (i.get() < target) {
                    synchronized (lockCA) {
                        try {
                            lockCA.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    synchronized (lockAB) {
                        System.out.println(Thread.currentThread() + ":" + i.getAndIncrement());
                        lockAB.notifyAll();
                    }
                }
            }
        }, "线程一");
        Thread b = new Thread(new Runnable() {
            @Override
            public void run() {
                while (i.get() < target) {
                    synchronized (lockAB) {
                        try {
                            lockAB.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    synchronized (lockBC) {
                        System.out.println(Thread.currentThread() + ":" + i.getAndIncrement());
                        lockBC.notifyAll();
                    }
                }
            }
        }, "线程二");
        Thread c = new Thread(new Runnable() {
            @Override
            public void run() {
                while (i.get() < target) {
                    synchronized (lockBC) {
                        try {
                            lockBC.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    synchronized (lockCA) {
                        System.out.println(Thread.currentThread() + ":" + i.getAndIncrement());
                        lockCA.notifyAll();
                    }
                }

            }
        }, "线程三");
        c.start();
        b.start();
        a.start();
        Thread.sleep(500);
        synchronized (lockCA) {
            lockCA.notifyAll();
        }
    }
}
