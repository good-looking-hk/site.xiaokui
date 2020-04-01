package site.xiaokui.common.hk.thread.lock;

import lombok.SneakyThrows;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author HK
 * @date 2020-03-28 16:06
 */
public class ConditionTest1 {

    ReentrantLock lock = new ReentrantLock(false);
    Condition notFull = lock.newCondition();
    Condition notEmpty = lock.newCondition();
    int[] datas = new int[4];
    int size = 0;

    private void put(int i) {
        lock.lock();
        try {
            while (size == datas.length) {
                notFull.await();
            }
            datas[size++] = i;
            System.out.println("存入" + i);
            notEmpty.signal();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    private int take() {
        lock.lock();
        try {
            while (size == 0) {
                notEmpty.await();
            }
            int t = datas[--size];
            System.out.println("取出" + t);
            notFull.signal();
            return t;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        return -1;
    }

    public static void main(String[] args) {
        ConditionTest1 test = new ConditionTest1();
        new Thread(new Runnable() {
            @Override
            public void run() {
                test.put(1);
                test.put(2);
                test.put(3);
                test.put(4);
                test.put(5);
            }
        }).start();
        new Thread(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                while (true) {
                    Thread.sleep(10);
                    test.take();
                }
            }
        }).start();
    }
}
