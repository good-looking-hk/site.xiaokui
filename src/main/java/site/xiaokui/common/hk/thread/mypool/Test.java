package site.xiaokui.common.hk.thread.mypool;

import lombok.SneakyThrows;
import redis.clients.jedis.Jedis;

import java.util.concurrent.CyclicBarrier;

/**
 * @author HK
 * @date 2020-04-17 16:17
 */
public class Test {
    public static void main(String[] args) {
        ClientPool pool = new ClientPool(4);
        CyclicBarrier cyclicBarrier = new CyclicBarrier(20);
        for (int i = 0; i < 20; i++) {
             new Thread(new Runnable() {
                @SneakyThrows
                @Override
                public void run() {
                    cyclicBarrier.await();
                    Client client = pool.getClient(100);
                    if (client == null) {
                        return;
                    }
                    client.execute();
                    client.close();
                }
            }).start();
        }
    }
}
