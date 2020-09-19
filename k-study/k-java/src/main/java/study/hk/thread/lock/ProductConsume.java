package study.hk.thread.lock;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author HK
 * @date 2019-02-27 15:35
 */
public class ProductConsume {

    private static final Object writeLock = new Object();

    private static final Object readLock = new Object();

    private static AtomicInteger n = new AtomicInteger(0);

    private static LinkedList<Integer> list = new LinkedList<>();

    private static int i = 0;

    public static class Product implements Runnable {
        @Override
        public void run() {
            while (true) {
                synchronized (readLock) {
                    list.add(i);
                    n.incrementAndGet();
                    System.out.println(Thread.currentThread().getName() + " send " + (i++));
                    readLock.notifyAll();
                }
                try {
                    Thread.sleep(600);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static class Consume implements Runnable {
        @Override
        public void run() {
            while (true) {
                while (n.get() > 0) {
                    n.decrementAndGet();
                    System.out.println(Thread.currentThread().getName() + " receive " + list.pollLast());
                }
//                synchronized (readLock) {
//                    try {
//                        readLock.wait();
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
            }
        }
    }

    public static void main(String[] args) {
        for (int i = 1; i <= 1; i++) {
            new Thread(new Product(), "p" + i).start();
            new Thread(new Consume(), "c" + i).start();
        }
    }
}
