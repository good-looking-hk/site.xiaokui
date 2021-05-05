package site.xiaokui.spring.core.util;

/**
 * @author HK
 * @date 2019-06-12 23:05
 */
public class Assert {

    public static void isFalse(boolean bool) {
        if (bool) {
            throw new RuntimeException("两对象相等");
        }
    }

    public static void isTrue(boolean bool) {
        if (!bool) {
            throw new RuntimeException("两对象不相等");
        }
    }

    public static void notNull(Object o) {
        if (o == null) {
            throw new NullPointerException("对象不能为空");
        }
    }

    public static void notNull(Object o, String msg) {
        if (o == null) {
            throw new NullPointerException(msg);
        }
    }

    public static void notNull(Object... os) {
        for (Object o : os) {
            if (o == null) {
                throw new NullPointerException("对象不能为空");
            }
        }
    }
}
