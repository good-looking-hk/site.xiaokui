package real;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author HK
 * @date 2021-03-01 10:11
 */
public class Temp {

    public static void main(String[] args) {
        Lock lock = new ReentrantLock();
        Thread a = new Thread(new Runnable() {
            @Override
            public void run() {
                lock.lock();
                try {
                    System.out.println("a get lock");
                } finally {
//                    lock.unlock();
                    System.out.println("a released lock");
                }
            }
        });

        Thread b = new Thread(new Runnable() {
            @Override
            public void run() {
                lock.lock();
                try {
                    System.out.println("b get lock");
                } finally {
                    lock.unlock();
//                    lock.notify();
                    System.out.println("b released lock");
                }
            }
        });
        a.start();
        b.start();
    }
}
