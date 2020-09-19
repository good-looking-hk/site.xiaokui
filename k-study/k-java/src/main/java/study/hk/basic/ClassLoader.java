package study.hk.basic;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 注意，代码块和构造器两者基本是绑在一起的，且代码块在构造器前面
 * @author HK
 * @date 2018-10-16 23:33
 */
public class ClassLoader {

    static class Father1 {
        private int father = 0;
        protected int father1 = 1;
        int father2 = 2;
        public int father3 = 3;
        private void father() {
        }
        void father1 () {
        }
        protected void father2() {
        }
        public void father3() {
        }
    }

    static class Son1 extends Father1 {
        private int son = 0;
        protected int son1 = 1;
        int son2 = 2;
        public int son3 = 3;
        private void son() {
        }
        void son1() {
        }
        protected void son2() {
        }
        public void son3() {
        }
    }

    public static void testReflect() {
        Class<?> cls = Son1.class;
        Field[] fields = cls.getDeclaredFields();
        System.out.println("本类声明的所有字段（包含private）");
        for (Field f : fields) {
            System.out.println(f);
        }
        System.out.println("本类及从父类继承的字段（只显示public）");
        fields = cls.getFields();
        for (Field f : fields) {
            System.out.println(f);
        }
        System.out.println("本类声明的所有方法（包含private）");
        Method[] methods = cls.getDeclaredMethods();
        for (Method m : methods) {
            System.out.println(m);
        }
        System.out.println("本类及从父类继承的方法（只显示public）");
        methods = cls.getMethods();
        for (Method m : methods) {
            System.out.println(m);
        }
    }

    static int t = DataType.test;

    static class A {
        static {
            System.out.println("A static");
        }
    }

    static class B {
        static {
            System.out.println("B static");
        }
    }

    private static void testClassLoad() throws Exception {
        Class a = Class.forName("common.hk.basic.ClassLoader$A");
        System.out.println("class a init");
        Class b = a.getClassLoader().loadClass("common.hk.basic.ClassLoader$B");
        System.out.println("class b not init");
        b.newInstance();
        System.out.println("class b init");
        System.out.println(1/0);
    }


    public static void main(String[] args) throws Exception {
        testReflect();
    }

    static class Son extends Father {
        static {
            System.out.println("son static");
        }

        {
            System.out.println("son code block");
        }

        public Son() {
            System.out.println("son constructor init");
        }
    }

    static class Father {
        static int a = 1;
        static {
            a = 1;
            System.out.println("father static");
        }
        {
            System.out.println("father code block");
        }
        public Father() {
            System.out.println("father constructor init");
        }
    }
}
