package real;

/**
 * 单例的实现（懒汉 和 饿汉） 工厂模式 策略模式 模板模式等的实现
 * @author HK
 * @date 2021-03-04 13:27
 */
public class Main10 {

    static  class Singleton {
        int i = 0;
        public Singleton () {
            System.out.println("我只初始化了一次！");
        }

        /**
         * 加上 synchronized 可以保证 print打印结果有序且正确
         * 否则，实际结果输出可能比预期小，由于线程冲突，会丢失几次 ++i
         */
        public void print() {
            System.out.println(Thread.currentThread().getName() +  ": i=" + ++i);
        }
    }

    /**
     * 饿汉 等不及提前初始化
     */
    static class Singleton1 {
        private static final Singleton singleton = new Singleton();
        public static Singleton getSingleton() {
            return singleton;
        }
        private Singleton1() {}
    }

    /**
     * 懒汉1 用时再去初始化
     */
    static class Singleton2 {
        private static Singleton singleton;
        public synchronized static Singleton getSingleton() {
            if (singleton == null) {
                singleton = new Singleton();
            }
            return singleton;
        }
        private Singleton2() {}
    }

    /**
     * 懒汉2 用时再去初始化
     */
    static class Singleton3 {
        // volatile 可解决指令重排序问题
        private static volatile Singleton singleton;
        public static Singleton getSingleton() {
            // 所有线程可以见
            if (singleton == null) {
                // 线程依次进入
                synchronized (Singleton3.class) {
                    // 只有一条线程会进行初始化操作，后续之间退出返回
                    if (singleton == null) {
                        // 这不存在指令重排序
                        singleton = new Singleton();
                    }
                }
            }
            return singleton;
        }
        private Singleton3() {}
    }

    /**
     * 懒汉3 - 依靠枚举
     */
    static class Singleton4 {
        enum Single {
            SINGLE;
            Single() {
                // TODO
            }
            void print() {
                // TODO
            }
        }
        public static Single getSingleton() {
            return Single.SINGLE;
        }
        private Singleton4() {}
    }

    /**
     * 懒汉4 - 将延迟推到子类的初始化
     */
    static class Singleton5 {
        static class Singleton {
            static final Singleton singleton = new Singleton();
        }
        public static Singleton getSingleton() {
            return Singleton.singleton;
        }
        private Singleton5() {}
    }

    /**
     * 如果不显式声明锁，即没有synchronized关键字，那么线程之间将有任何的同步措施
     * 对于静态方法，锁对象是它的class对象
     * 对于非静态方法，锁对象是this
     * 对于指定同步代码块，锁是synchronized指定的所对象
     * 多线程测试
     */
    public static void main(String[] args) {
        Thread a = new Thread(new Runnable() {
            @Override
            public void run() {
                int i = 0;
                while (i++ < 20) {
                    Singleton1.getSingleton().print();
                    Singleton2.getSingleton().print();
                    Singleton3.getSingleton().print();
                }
            }
        }, "线程a");
        Thread b = new Thread(new Runnable() {
            @Override
            public void run() {
                int i = 0;
                while (i++ < 20) {
                    Singleton1.getSingleton().print();
                    Singleton2.getSingleton().print();
                    Singleton3.getSingleton().print();
                }
            }
        }, "线程b");
        a.start();
        b.start();
    }
}
