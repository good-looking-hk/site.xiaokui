package site.xiaokui.common.hk.thread.basic;

/**
 * @author HK
 * @date 2018-10-13 23:23
 */
public class Yeild {
    volatile static boolean notStart = true;

    static String count = "0";

    public static void main(String[] args) {
        try {
            test1();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void test1() throws InterruptedException {
        Thread[] threads = new Thread[100];
        Object lock = new Object();
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(new Person("线程" + i, lock));
            threads[i].start();
        }
        notStart = false;
    }

    static class Person implements Runnable {
        String name;
        Object lock;

        public Person(String name, Object lock) {
            this.name = name;
            this.lock = lock;
        }

        @Override
        public void run() {
            while (notStart) {
                Thread.yield();
            }
            synchronized (lock) {
                if (!"10".equals(count)) {
                    count = String.valueOf(Integer.valueOf(count) + 1);
                    System.out.println(name + "买到了第" + count + "张票");
                }
            }
        }
    }
}
