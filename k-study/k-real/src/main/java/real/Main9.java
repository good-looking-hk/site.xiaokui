package real;

/**
 * 共计9个苹果，有2只猴子，一个猴子每次拿2个苹果，一个猴子每次拿3个苹果，如果剩余的苹果不够猴子每次拿的数量，则2只猴子停止拿苹果，请用java多线程模拟上面的描述并打印出过程
 * @author HK
 * @date 2021-03-04 10:57
 */
public class Main9 {

    static Object lock = new Object();

    static int nineApple = 9;

    public static void main(String[] args) {
        Thread a = new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (lock) {
                    while (nineApple - 3 > 0) {
                        nineApple -= 3;
                        System.out.println(Thread.currentThread().getName() + " 拿了 3个苹果，还剩余 " + nineApple);
                    }
                }

            }
        }, "猴子一");

        Thread b = new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (lock) {
                    while (nineApple - 2 > 0) {
                        nineApple -= 2;
                        System.out.println(Thread.currentThread().getName() + " 拿了 2个苹果，还剩余 " + nineApple);
                    }
                }
            }
        }, "猴子二");
        a.start();
        b.start();
    }
}
