package temp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * @author HK
 * @date 2018-08-28 20:18
 */
public class Test {
    public static void main(String[] args) {
        ArrayList a;
        LinkedList b;
        HashMap c;
        char[] test1 = new char[]{'1', '2', '3'};
        char[] test2 = test1;
        test1[0] = '4';
        //结果是4，说明数组地址也是个引用，即数组不是原子类型
        System.out.println(test2[0]);

        //简单复制耗时：0ms:ϧ
        //native方法复制耗时：0ms:ϧ
        testCopyArray(1000);
        //简单复制耗时：0ms:蚟
        //native方法复制耗时：0ms:蚟，此时还看不出差距，往下
        testCopyArray(100000);

        //简单复制耗时：32ms:陿
        //native方法复制耗时：15ms:陿
        testCopyArray(10000000);
        //简单复制耗时：47ms:，生僻字已经显示不出了
        //native方法复制耗时：78ms:
        testCopyArray(100000000);
    }

    private static void testCopyArray(int length) {
        char[] chars1 = new char[length];
        for (int i = 0; i < length; i++) {
            chars1[i] = (char)i;
        }
        char[] chars1_copy1 = new char[length];
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < length; i++) {
            chars1_copy1[i] = chars1[i];
        }
        long endTime = System.currentTimeMillis();
        System.out.print("简单复制耗时：" + (endTime - startTime) + "ms:");
        System.out.println(chars1_copy1[length - 1]);

        char[] chars1_copy2 = new char[length];
        startTime = System.currentTimeMillis();
        System.arraycopy(chars1, 0, chars1_copy2, 0, length);
        endTime = System.currentTimeMillis();
        System.out.print("native方法复制耗时：" + (endTime - startTime) + "ms:");
        System.out.println(chars1_copy2[length - 1]);
    }
}
