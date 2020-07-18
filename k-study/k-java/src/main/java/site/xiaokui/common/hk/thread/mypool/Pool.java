package site.xiaokui.common.hk.thread.mypool;


import java.util.LinkedList;

/**
 * @author HK
 * @date 2020-04-17 16:00
 */
public class Pool<T> {

    protected LinkedList<T> pool = new LinkedList<>();

    private int failTimes;

    T getClient() {
        return getClient(0);
    }

    T getClient(long mills) {
        synchronized (pool) {
            while (pool.isEmpty() && mills > 0) {
                try {
                    long cur = System.currentTimeMillis();
                    pool.wait(mills);
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
        }
    }

    void returnPool(T t) {
        synchronized (pool) {
            pool.addLast(t);
            pool.notify();
        }
    }

    static class WrapperObject {

    }

}
