package study.hk.thread.pool;

import java.sql.Connection;
import java.util.LinkedList;

/**
 * @author HK
 * @date 2018-07-22 23:30
 */
public class ConnectionPool {

    private LinkedList<Connection> pool = new LinkedList<>();

    public ConnectionPool(int initialSize) {
        if (initialSize > 0) {
            for (int i = 0; i < initialSize; i++) {
                pool.addLast(ConnectionDriver.createConnection());
            }
        }
    }

    public void releaseConnection(Connection connection) {
        if (connection != null) {
            synchronized (pool) {
                // 连接释放需要进行通知，这样其他消费者能够感知连接池中已经归还了一个连接
                pool.addLast(connection);
                pool.notifyAll();
            }
        }
    }

    // 在mills毫秒内无法获取到连接，将会返回null
    public Connection fetchConnection(long mills) throws InterruptedException {
        synchronized (pool) {
            // 完全超时
            if (mills <= 0) {
                while (pool.isEmpty()) {
                    pool.wait();
                }
                return pool.removeFirst();
            } else {
                long future = System.currentTimeMillis() + mills;
                long remaining = mills;
                while (pool.isEmpty() && remaining > 0) {
                    pool.wait(remaining);
                    // 如果remaining还大于0，说明还有时间可以继续等待连接进来，反之就已经超时了
                    remaining = future - System.currentTimeMillis();
                }
                Connection result = null;
                // 由于拥有pool的锁，所以这里的isEmpty结果和上面isEmpty的结果是一样的
                if (!pool.isEmpty()) {
                    result = pool.removeFirst();
                }
                return result;
            }
        }
    }
}
