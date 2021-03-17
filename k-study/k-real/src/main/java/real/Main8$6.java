package real;

/**
 * @author HK
 * @date 2021-03-15 14:47
 */
public class Main8$6 {

    //一个线程打印1 2 3 ...
//一个线程打印a b c ...
//交替打印 1 a 2 b 3 c  ... 直到所有字母打印完毕
    private static int index = 1;
    private static Object o = new Object();

    public static void main(String[] args) {
        new Thread() {
            @Override
            public void run() {
                synchronized (o) {
                    while (index <= 26) {
                        System.out.println(index++);
                        //唤醒B线程
                        System.out.println(1111);
                        o.notify();
                        System.out.println(2222);
                        if (index == 27) {
                            //最后一次不能再等待，否则会死等
                            break;
                        }

                        //阻塞自己
                        try {
                            System.out.println(3333);
                            o.wait();
                            System.out.println(4444);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }.start();

        new Thread() {
            @Override
            public void run() {
                synchronized (o) {
                    while (index <= 27) {
                        char c = (char) ('a' + (index - 2));
                        System.out.println(c);

                        if (index == 27) {
                            //最后一次不能再等待，否则会一直死等
                            break;
                        }
                        //唤醒A线程
                        System.out.println(5555);
                        o.notify();
                        System.out.println(6666);
                        //阻塞自己
                        try {
                            System.out.println(7777);
                            o.wait();
                            System.out.println(8888);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
        }.start();
    }
}

