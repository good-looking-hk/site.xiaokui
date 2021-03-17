package real;

import java.util.concurrent.locks.LockSupport;

/**
 * 3个线程轮流打印 1 2 3 4 5 6 7 8 9 - 完美通过
 * <p>
 * 使用 LockSupport 工具类实现，代码真的是简洁
 *
 * @author HK
 * @date 2021-03-15 16:42
 */
public class Main3$4 {

    static Thread a, b, c;

    static int startValue = 1, endValue = 8;

    public static void main(String[] args) {
        a = new Thread(new Runnable() {
            @Override
            public void run() {
                while (startValue < endValue) {
                    // 暂停指定线程
                    LockSupport.park(a);
                    System.out.println(Thread.currentThread().getName() + ":" + startValue++);
                    // 唤醒指定线程
                    LockSupport.unpark(b);
                }
            }
        }, "线程a");
        b = new Thread(new Runnable() {
            @Override
            public void run() {
                while (startValue < endValue) {
                    LockSupport.park(b);
                    System.out.println(Thread.currentThread().getName() + ":" + startValue++);
                    LockSupport.unpark(c);
                }
            }
        }, "线程b");
        c = new Thread(new Runnable() {
            @Override
            public void run() {
                while (startValue < endValue) {
                    LockSupport.park(c);
                    System.out.println(Thread.currentThread().getName() + ":" + startValue++);
                    LockSupport.unpark(a);
                }
            }
        }, "线程c");

        c.start();
        b.start();
        a.start();
        LockSupport.unpark(a);
    }
}
