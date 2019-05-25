package site.xiaokui.common.hk.offer;

/**
 * 实现Singleton模式
 * 对于这个题目，只要对多线程知识有稍微深一点的了解，做起来其实很简单
 * @author HK
 * @date 2018-08-04 16:12
 */
public class Singleton {

    /**
     * 最简单的一种单例（又称饿汉单例），在类第一次加载时就初始化单例
     */
    static class Singleton1 {
        /**
         * 类加载时就已经初始化了单例
         */
        private static final Singleton1 SINGLETON_1 = new Singleton1();

        private Singleton1(){}

        public static Singleton1 getInstance() {
            return SINGLETON_1;
        }
    }

    /**
     * 一种稍稍高级一点的单例-使用内部类，按需加载，线程安全
     */
    static class Singleton2 {
        private static class Singleton3 {
            private static Singleton2 singleton2 = new Singleton2();
        }

        private Singleton2(){}

        /**
         * 第一次初始化Singleton3类时，会有锁机制保证类只初始化一次，则singleton2只会被初始化一次
         */
        public Singleton2 getInstance() {
            return Singleton3.singleton2;
        }
    }

    /**
     * 还有一种就是Effective Java作者Josh Bloch提倡的写法，枚举单例，也是按需加载，线程安全
     */
    enum Singleton4 {
        SINGLETON_4;
        public void doSomething() {}
    }

    /**
     * 其实众所周知的双重检查机制的单例还是存在错误的，但可以通过一些弥补措施来补救（不推荐）
     */
    static class Singleton5 {
        /** 使用volatile关键字来禁止对singleton5的操作重排序，使volatile写操作对后续线程可见 */
        private volatile static Singleton5 singleton5;

        private Singleton5(){}

        public static Singleton5 getInstance() {
            if (singleton5 == null) {
                synchronized (Singleton5.class) {
                    if (singleton5 == null) {
                        // 由于这一步存在指令重排序，因此线程不安全，可以通过volatile关键字来禁止重排序
                        singleton5 = new Singleton5();
                    }
                }
            }
            return singleton5;
        }
    }
}
