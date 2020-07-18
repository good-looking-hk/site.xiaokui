import lombok.SneakyThrows;
import sun.misc.Unsafe;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author HK
 * @date 2020-05-27 19:53
 */
public class UnsafeTest {

    private static volatile Integer state = 0;

    private static AtomicInteger curValue = new AtomicInteger(1);;

    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {
        // 注意不能通过方法反射，可以通过字段或私有构造方法反射
        Field field = Unsafe.class.getDeclaredField("theUnsafe");
        field.setAccessible(true);
        Unsafe unsafe = (Unsafe)field.get(null);

        Constructor<Unsafe> constructor = Unsafe.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        unsafe = constructor.newInstance();

        // Unsafe实例后，下面将模拟一个场景，三个线程轮流打印123 456 789
        new Runner(unsafe, state, 1).start();
        new Runner(unsafe, state, 2).start();
        new Runner(unsafe, state, 3).start();

        while (curValue.get() < 9) {
        }
    }

    private static class Runner extends Thread {
        Integer state, expect;
        Unsafe unsafe;
        public Runner (Unsafe unsafe, Integer state, int expect) {
            super("线程" + expect);
            this.unsafe = unsafe;
            this.state = state;
            this.expect = expect;
        }

        @SneakyThrows
        @Override
        public void run () {
            Field value = state.getClass().getDeclaredField("value");
            long offset = unsafe.objectFieldOffset(value);
            value.setAccessible(true);
            while (curValue.get() < 9) {
                while (unsafe.compareAndSwapInt(state, offset,expect - 1, expect)) {
                    System.out.println(Thread.currentThread().getName() + ":" + curValue.getAndIncrement() + " " + state);
                    if (state == 3) {
                        unsafe.compareAndSwapInt(state, offset,3, 0);
                    }
                }
            }
        }
    }
}
