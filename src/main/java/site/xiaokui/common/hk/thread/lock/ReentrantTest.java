package site.xiaokui.common.hk.thread.lock;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author HK
 * @date 2018-10-11 22:06
 */
public class ReentrantTest {

    private static Lock fairLock = new ReetrantLock2(true);
    private static Lock unfairLock = new ReetrantLock2(false);

    public void fair() {
        System.out.println("公平锁测试");
        testLock(fairLock);
    }

    public void unfair() {
        System.out.println("非公平锁测试");
        testLock(unfairLock);
    }

    private void testLock(Lock lock) {
        for (int i = 0; i < 6; i++) {
            Thread thread = new Job(lock);
            thread.start();
        }
    }

    private static class Job extends Thread {
        private Lock lock;
        public Job(Lock lock) {
            this.lock = lock;
        }
        @Override
        public void run() {
            while (true) {
                lock.lock();
                try {
                    ReetrantLock2 temp = (ReetrantLock2) lock;
                    System.out.println("Lock by [" + getName() + "],Waiting by " + temp.getQueuedThreads());
                    System.out.println("Lock by [" + getName() + "],Waiting by " + temp.getQueuedThreads());
                } finally {
                    lock.unlock();
                }
            }
        }
        @Override
        public String toString() {
            return getName();
        }
    }

    private static class ReetrantLock2 extends ReentrantLock {
        public ReetrantLock2(boolean fair) {
            super(fair);
        }
        @Override
        public Collection<Thread> getQueuedThreads() {
            List<Thread> list = new ArrayList<>(super.getQueuedThreads());
            Collections.reverse(list);
            return list;
        }
    }
}
