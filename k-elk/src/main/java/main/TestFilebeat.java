package main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author HK
 * @date 2020-10-02 16:42
 */
public class TestFilebeat {

    private static final Logger log = LoggerFactory.getLogger(TestFilebeat.class);

    public static void main(String[] args) {
        while (true) {
            try {
                Thread.sleep(300);
                log.info("info收到客户端请求" + System.currentTimeMillis());
                log.warn("warn消息" + System.currentTimeMillis());
            } catch (InterruptedException e) {

            }
        }
    }
}
