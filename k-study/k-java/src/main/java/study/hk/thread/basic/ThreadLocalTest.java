package study.hk.thread.basic;

/**
 * @author HK
 * @date 2019-10-11 10:10
 */
public class ThreadLocalTest {

    static class Obj {
        String name;
        Obj next;

        public Obj(String name, Obj next) {
            this.name = name;
            this.next = next;
        }

        @Override
        public String toString() {
            String str = next != null ? next.name : "null";
            return "name:" + name + "，next name:" + str;
        }
    }

    private static ThreadLocal<Obj> threadLocal = new ThreadLocal<>();

    private static Obj objA = new Obj("a", null);
    private static Obj objB = new Obj("b", objA);

    private static volatile boolean isEnd = false;

    public static void main(String[] args) throws InterruptedException {
        Thread a = new Thread(new Runnable() {
            @Override
            public void run() {
                threadLocal.set(objA);
                while (!isEnd) {
                }
                System.out.println(Thread.currentThread().getName() + ":" + threadLocal.get());
            }
        }, "线程a");
        Thread b = new Thread(new Runnable() {
            @Override
            public void run() {
                threadLocal.set(objB);
                while (!isEnd) {
                }
                System.out.println(Thread.currentThread().getName() + ":" + threadLocal.get());
                threadLocal.remove();
            }
        }, "线程b");
        a.start();
        b.start();
        Thread.sleep(200);
        System.out.println(objA);
        System.out.println(objB);
        objA = null;
        objB = null;
        isEnd = true;
        System.out.println(Thread.currentThread().getName() + ":" + threadLocal.get());
    }
}
