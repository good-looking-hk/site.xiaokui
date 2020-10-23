package study.hk.wx;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

/**
 * @author HK
 * @date 2020-04-29 14:08
 */
public class Test {

    public static void main(String[] args) {
        float a = 1.0f - 0.9f;
        float b = 0.9f - 0.8f;
        if (a == b) {
            // 预期进入此代码快，执行其它业务逻辑 // 但事实上 a==b 的结果为 false }
            System.out.println(1);
        }
        Float x = Float.valueOf(a);
        Float y = Float.valueOf(b);
        if (x.equals(y)) { // 预期进入此代码快，执行其它业务逻辑 // 但事实上 equals 的结果为 false }
            System.out.println(2);
        }

        new BigDecimal("0.2");
        BigDecimal.valueOf(0.2);

        ArrayList list = new ArrayList<>();
        list.add("1");
        list.add("2");
        list.add("3");
        list.add("4");
        System.out.println(list);

        List<String> subList = list.subList(0, 2);
        System.out.println(subList);

        subList.add("5");
        System.out.println(list);
        System.out.println(subList);
        TreeSet set;

        list.add(0, "0");
        System.out.println(list);
        // Exception in thread "main" java.util.ConcurrentModificationException
        System.out.println(subList);
    }
}
