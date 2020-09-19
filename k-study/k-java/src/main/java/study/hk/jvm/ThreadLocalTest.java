package study.hk.jvm;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author HK
 * @date 2018-12-31 14:04
 */
public class ThreadLocalTest {

    static ThreadLocal<String> threadLocal = new ThreadLocal<>();

    static AtomicInteger i = new AtomicInteger(0);

    public static void main(String[] args) {
        while (true) {
            if (i.get() > 3) {
                return;
            }
            int temp = i.incrementAndGet();
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    if (temp == 1) {
                        threadLocal.set("小明");
                    } else if (temp == 2) {
                        threadLocal.set("小黑");
                    } else if (temp == 3) {
                        threadLocal.set("小白");
                    }
                    while(true) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Thread cur = Thread.currentThread();
                        System.out.println(cur.getName() + "正在服务于" + threadLocal.get());
                    }
                }
            }, "线程" + temp);
            thread.start();
        }
    }
}
