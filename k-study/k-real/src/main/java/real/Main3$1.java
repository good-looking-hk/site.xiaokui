package real;

/**
 * 3个线程轮流打印 1 2 3 4 5 6 7 8 9 - 完美通过
 * @author HK
 * @date 2021-02-27 16:17
 */
public class Main3$1 {

    private static int startValue = 0, endValue = 7;

    public static void main(String[] args) throws InterruptedException {
        Object aLock = new Object();
        Object bLock = new Object();
        Object cLock = new Object();

        Thread a = new Thread(new Runnable() {
            @Override
            public void run() {
                while (startValue < endValue) {
                    synchronized (cLock) {
                        try {
                            cLock.wait();
                            System.out.println(Thread.currentThread().getName() + ":" + ++startValue);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    synchronized (aLock) {
                        aLock.notify();
                    }
                }
            }
        }, "线程a");
        Thread b = new Thread(new Runnable() {
            @Override
            public void run() {
                while (startValue < endValue) {
                    synchronized (aLock) {
                        try {
                            aLock.wait();
                            System.out.println(Thread.currentThread().getName() + ":" + ++startValue);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    synchronized (bLock) {
                        bLock.notify();
                    }
                }
            }
        }, "线程b");
        Thread c = new Thread(new Runnable() {
            @Override
            public void run() {
                while (startValue < endValue) {
                    synchronized (bLock) {
                        try {
                            bLock.wait();
                            System.out.println(Thread.currentThread().getName() + ":" + ++startValue);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    synchronized (cLock) {
                        cLock.notify();
                    }
                }
            }
        }, "线程c");
        a.start();
        b.start();
        c.start();
        Thread.sleep(500);
        synchronized (cLock) {
            cLock.notify();
        }
    }
}
