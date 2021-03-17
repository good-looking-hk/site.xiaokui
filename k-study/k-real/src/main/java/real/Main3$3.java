package real;

/**
 * 3个线程轮流打印 1 2 3 4 5 6 7 8 9 - 完美通过
 *
 * 哈哈哈，代码鬼才有木有
 *
 * @author HK
 * @date 2021-03-15 16:33
 */
public class Main3$3 {

    /**
     * 1-a线程打印 2-b线程打印 3-c线程打印
     */
    volatile static int flag = 1;

    static int startValue = 1, endValue = 9;

    public static void main(String[] args) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (startValue <= endValue) {
                    if (flag == 1) {
                        System.out.println(Thread.currentThread().getName() + ":" + startValue++);
                        flag = 2;
                    }
                }
            }
        }, "线程a").start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (startValue <= endValue) {
                    if (flag == 2) {
                        System.out.println(Thread.currentThread().getName() + ":" + startValue++);
                        flag = 3;
                    }
                }
            }
        }, "线程b").start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (startValue <= endValue) {
                    if (flag == 3) {
                        System.out.println(Thread.currentThread().getName() + ":" + startValue++);
                        flag = 1;
                    }
                }
            }
        }, "线程c").start();
    }
}
