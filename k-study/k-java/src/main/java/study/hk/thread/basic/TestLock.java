package study.hk.thread.basic;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * [42823984339490, 42823988185681, 42823990574573, 42823993687728, 42823994159435, 42823994400717, 42823995633337, 42823996246158, 42823998608640, 42823998708018]
 * [42824999777141, 42824999759928, 42824999750664, 42824999722085, 42824999711352, 42824999689751, 42824999676974, 42824999662744, 42824999519331, 42824999495594]
 * <<<<<<<<<
 * >>>>>>>>>
 * @author HK
 * @date 2018-10-13 22:32
 */
public class TestLock {

    static Object lock = new Object();

    static boolean start = false;

    static class Worker implements Runnable {
        long startTime;
        long wakeupTime;
        @Override
        public void run() {
            synchronized (lock) {
                while (!start) {
                    try {
                        startTime = System.nanoTime();
                        lock.wait();
                    } catch (InterruptedException e){
                    }
                }
                wakeupTime = System.nanoTime();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Thread[] threads = new Thread[10];
        Worker[] workers = new Worker[threads.length];
        for (int i = 0; i < threads.length; i++) {
            workers[i] = new Worker();
            threads[i] = new Thread(workers[i], String.valueOf(i + 1));
            threads[i].start();
        }
        TimeUnit.SECONDS.sleep(1);
        start = true;
        for (int i = 0; i < threads.length; i++) {
            synchronized (lock) {
                lock.notify();
            }
        }
//        synchronized (lock) {
//            start = true;
//            lock.notifyAll();
//        }
        long[] startTimes = new long[workers.length];
        long[] wakeupTimes = new long[workers.length];
        for (int i = 0; i < threads.length; i++) {
            threads[i].join();
            startTimes[i] = workers[i].startTime;
            wakeupTimes[i] = workers[i].wakeupTime;
        }
        System.out.println(Arrays.toString(startTimes));
        System.out.println(Arrays.toString(wakeupTimes));
        for (int i = 0; i < threads.length - 1; i++) {
            System.out.print(startTimes[i] > startTimes[i + 1] ? ">" : "<");
        }
        System.out.println();
        for (int i = 0; i < threads.length - 1; i++) {
            System.out.print(wakeupTimes[i] > wakeupTimes[i + 1] ? ">" : "<");
        }
    }


}
