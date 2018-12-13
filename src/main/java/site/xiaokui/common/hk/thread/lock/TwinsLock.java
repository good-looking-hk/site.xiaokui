package site.xiaokui.common.hk.thread.lock;

import site.xiaokui.common.hk.thread.basic.ThreadState;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * @author HK
 * @date 2018-10-11 15:18
 */
public class TwinsLock implements Lock {

    private final Sync sync = new Sync(2);

    private static final class Sync extends AbstractQueuedSynchronizer {
        Sync(int count) {
            if (count <= 0) {
                throw new IllegalArgumentException("参数必须大于0");
            }
            setState(count);
        }

        @Override
        public int tryAcquireShared(int reduceCount) {
            for (;;) {
                int current = getState();
                int newCount = current - reduceCount;
                if (newCount < 0 || compareAndSetState(current, newCount)) {
                    return newCount;
                }
            }
        }

        @Override
        public boolean tryReleaseShared(int returnCount) {
            for (;;) {
                int current = getState();
                int newCount = current + returnCount;
                if (compareAndSetState(current, newCount)) {
                    return true;
                }
            }
        }
    }

    @Override
    public void lock() {
        sync.acquireShared(1);
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {

    }

    @Override
    public boolean tryLock() {
        return false;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public void unlock() {
        sync.releaseShared(1);
    }

    @Override
    public Condition newCondition() {
        return null;
    }

    public static void main(String[] args) {
        final Lock lock = new TwinsLock();
        class Worker extends Thread {
            @Override
            public void run() {
                while (true) {
                    lock.lock();
                    try {
                        ThreadState.SleepUtils.second(1);
                        System.out.println(Thread.currentThread().getName());
                        ThreadState.SleepUtils.second(1);
                    } finally {
                        lock.unlock();
                    }
                }
            }
        }
        for (int i = 0; i < 10; i++) {
            Worker w = new Worker();
            w.setDaemon(true);
            w.start();
        }
        for (int i = 0; i < 10; i++) {
            ThreadState.SleepUtils.second(1);
            System.out.println();
        }
    }
}
