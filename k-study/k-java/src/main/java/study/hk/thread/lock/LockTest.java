package study.hk.thread.lock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author HK
 * @date 2018-10-09 07:17
 */
public class LockTest {

    ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    Lock readLock = lock.readLock();
    Lock writeLock = lock.writeLock();
    boolean update = false;

    public void processData() {
        readLock.lock();
        if (!update) {
            // 必须先释放锁
            readLock.unlock();
            // 锁降级从写锁获取开始
            writeLock.lock();
            try {
                if (!update) {
                    // 准备数据的流程（略）
                    update = true;
                }
                readLock.lock();
            } finally {
                writeLock.unlock();
            }
        }
        try {
            // 使用数据的流程（略）
        } finally {
            readLock.unlock();
        }
    }
}
