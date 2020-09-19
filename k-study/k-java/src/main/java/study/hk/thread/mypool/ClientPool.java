package study.hk.thread.mypool;

/**
 * @author HK
 * @date 2020-04-17 16:12
 */
public class ClientPool extends Pool1<Client> {

    ClientPool(int initSize) {
        if (initSize < 1) {
            throw new IllegalArgumentException();
        }
        for (int i = 0; i < initSize; i++) {
            this.pool.add(new Client("客户端" + i, this));
        }
    }
}
