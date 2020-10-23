package study.hk.thread.lock;

import study.hk.thread.basic.ThreadState;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author HK
 * @date 2018-10-13 14:53
 */
public class BoundedQueue<T> {
    private Object[] items;
    private int addIndex, removeIndex, count;
    private Lock lock = new ReentrantLock();
    private Condition canAdd = lock.newCondition();
    private Condition canRemove = lock.newCondition();

    public BoundedQueue(int size) {
        items = new Object[size];
    }

    public void add(T t) {
        lock.lock();
        try {
            while (count == items.length) {
                System.out.println("已到达最大数量，不能再添加");
                canAdd.await();
            }
            items[addIndex] = t;
            System.out.println("放入位置" + addIndex + "成功：" + t);
            if (++addIndex == items.length) {
                addIndex = 0;
            }
            ++count;
            canRemove.signal();
        } catch (InterruptedException e) {
        } finally {
            lock.unlock();
        }
    }

    @SuppressWarnings("unchecked")
    public T remove() {
        lock.lock();
        try {
            while (count == 0) {
                System.out.println("数量为0，等待添加");
                canRemove.await();
            }
            Object x = items[removeIndex];
            System.out.println("从位置" + removeIndex + "取出成功：" + x);
            if (++removeIndex == items.length) {
                removeIndex = 0;
            }
            --count;
            canAdd.signal();
            return (T) x;
        } catch (InterruptedException e) {
            throw new RuntimeException();
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        BoundedQueue<Integer> queue = new BoundedQueue<>(3);
        Thread put = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 5; i++) {
                    queue.add(new Integer(i));
                }
            }
        });
        Thread get = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    queue.remove();
                }
            }
        });
        put.setDaemon(true);
        get.setDaemon(true);
        put.start();
        get.start();
        ThreadState.SleepUtils.second(1);
    }
}
