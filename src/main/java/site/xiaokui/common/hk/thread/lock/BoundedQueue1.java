package site.xiaokui.common.hk.thread.lock;

import site.xiaokui.common.hk.thread.basic.ThreadState;

/**
 * @author HK
 * @date 2018-10-13 19:27
 */
public class BoundedQueue1<T> {

    Object[] items;

    Object lock = new Object();

    private int addIndex, removeIndex, count;

    public BoundedQueue1(int size) {
        items = new Object[size];
    }

    public void add(T t) {
        synchronized (lock) {
            if (count == items.length) {
                System.out.println("已到达最大数量，不能再添加");
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                }
            }
            items[addIndex] = t;
            System.out.println("放入位置" + addIndex + "成功：" + t);
            if (++addIndex == items.length) {
                addIndex = 0;
            }
            ++count;
            lock.notify();
        }
    }

    public void remove() {
        synchronized (lock) {
            if (count == 0) {
                System.out.println("数量为0，等待添加");
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                }
            }
            T x = (T) items[removeIndex];
            System.out.println("从位置" + removeIndex + "取出成功：" + x);
            if (++removeIndex == items.length) {
                removeIndex = 0;
            }
            --count;
            lock.notify();
        }
    }

    public static void main(String[] args) {
        BoundedQueue1<Integer> queue = new BoundedQueue1<>(3);
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
