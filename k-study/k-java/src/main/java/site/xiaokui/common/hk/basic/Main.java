package site.xiaokui.common.hk.basic;

import java.util.ArrayList;
import java.util.List;

/**
 * @author HK
 * @date 2019-03-10 22:29
 */
public class Main {


    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        list.add("A");
        list.add("B");
        List<String> list1 = exchange(list);
        System.out.println(list.size());
        System.out.println(list1.size());
    }

    public static List<String> exchange(List<String> list) {
        list.add("C");
        list.add("D");
        list = new ArrayList<>();
        list.add("E");
        list.add("F");
        return list;
    }

    int a = 1;
    static int b = 2;
    B ab = new B();
    static {
        System.out.println("执行静态代码,b=" + ++b);
    }

    {
        System.out.println("执行构造代码块,a=" + ++a + ",b=" + ++b);
    }
    public Main() {
        System.out.println("执行构造方法,a=" + ++a + ",b=" + ++b);
    }

    static class A {
        static {
            System.out.println("初始化A");
        }
    }

    static class B extends A {
        static {
            System.out.println("初始化B");
        }
    }

}
