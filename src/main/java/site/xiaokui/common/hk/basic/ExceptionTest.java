package site.xiaokui.common.hk.basic;

/**
 * @author HK
 * @date 2018-11-03 21:27
 */
public class ExceptionTest {


    public static int test4() {
        int b = 20;
        try {
            System.out.println("try block");
            b = b / 0;
            return b += 80;
        } catch (Exception e) {
            b += 15;
            System.out.println("catch block");
        } finally {
            System.out.println("finally block");
            if (b > 25) {
                System.out.println("b>25, b = " + b);
            }
            b += 50;
        }
        return b;
    }

    static class GrandFather extends RuntimeException {
        GrandFather(String msg, Throwable ex) {
            super(msg);
            initCause(ex);
        }
        protected void print() {
            System.out.print("grandfather exception happen nested is :");
            Throwable cause = this.getCause();
            while (cause != null) {
                if (cause instanceof GrandFather) {
                    ((GrandFather) cause).print();
                }
                cause = cause.getCause();
            }
        }
    }

    static class Father extends GrandFather {
        public Father(String msg, Throwable ex) {
            super(msg, ex);
        }
        @Override
        protected void print() {
            System.out.print("father exception happen nested is :");
        }
    }

    static class Son extends Father {
        public Son(String msg, Throwable ex) {
            super(msg, ex);
        }
        @Override
        protected void print() {
            System.out.print("son exception happen nested is :");
        }
    }

    public static void main(String[] args) {
        try {
            System.out.println("开始嵌套异常测试");
            throw new GrandFather("grandfather", new Father("father", new Son("son", null)));
        } catch (GrandFather e) {
            e.print();
        }
        out();
    }

    private static void out() {
        try {
            System.out.println("\n执行最外层代码");
            mid();
        } catch (GrandFather e) {
            GrandFather grandFather = new GrandFather("2", e);
            grandFather.print();
        }
    }

    private static void mid() {
        try {
            System.out.println("执行中层代码");
            in();
        } catch (Father e) {
            throw new Father("1", e);
        }
    }

    private static void in() {
        try {
            System.out.println("执行最里层代码，发生异常");
            throw new Son(null, null);
        } catch (Son e) {
            throw e;
        }
    }
}
