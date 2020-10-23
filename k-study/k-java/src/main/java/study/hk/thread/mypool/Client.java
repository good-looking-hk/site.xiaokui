package study.hk.thread.mypool;

/**
 * @author HK
 * @date 2020-04-17 16:03
 */
public class Client {

    String name;

    Pool1 pool;

    Client(String name, Pool1 pool) {
        this.name = name;
        this.pool = pool;
    }

    void execute() {
        try {
            System.out.println(name + "执行请求");
            Thread.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    void close() {
        pool.returnPool(this);
    }
}
