import sun.misc.Unsafe;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

/**
 * @author HK
 * @date 2020-05-27 19:53
 */
public class UnsafeTest {

    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {
        // 注意不能通过方法反射，可以通过字段或私有构造方法反射
        Field field = Unsafe.class.getDeclaredField("theUnsafe");
        field.setAccessible(true);
        Unsafe unsafe = (Unsafe)field.get(null);

        Constructor<Unsafe> constructor = Unsafe.class.getDeclaredConstructor(null);
        constructor.setAccessible(true);
        unsafe = constructor.newInstance(null);
        // 通道Unsafe实例后，下面将模拟一个场景，三个线程轮流打印123 456 789
        long time = System.currentTimeMillis();
        System.out.println(Long.toHexString(time));

        System.out.println(Integer.toHexString((int) time));
    }
}
