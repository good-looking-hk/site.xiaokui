package study.hk.thread.mypool;

import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author HK
 * @date 2020-04-17 16:00
 */
public class Pool1<T> {

    protected LinkedList<T> pool = new LinkedList<>();

    private int failTimes;

    ReentrantLock lock = new ReentrantLock();

    Condition canGet = lock.newCondition();

    T getClient() {
        return getClient(0);
    }

    T getClient(long mills) {
        lock.lock();
        try {
            while (pool.isEmpty() && mills > 0) {
                try {
                    long cur = System.currentTimeMillis();
                    canGet.awaitNanos(mills);
                    mills -= System.currentTimeMillis() - cur;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            T obj = pool.pollLast();
            if (obj == null) {
                System.out.println("请求失败数" + ++failTimes);
            }
            return obj;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        return null;
    }

    void returnPool(T t) {
        lock.lock();
        try {
            pool.addLast(t);
            canGet.signal();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}
