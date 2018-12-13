package site.xiaokui.common.hk.basic;

/**
 * @author HK
 * @date 2018-10-13 15:53
 */
public class DataType {

    protected  static int test = 0;

    public static void main(String[] args) {
//        testDetail();
//        testParse();
        int a = 0;
    }

    private static void testDetail() {
        int a = 10 >> 1;
        int b = a++;
        int c = ++a;
        int d = b * a++;
        System.out.print(a + " ");
        System.out.print(b + " ");
        System.out.print(c + " ");
        System.out.print(d);
    }

    private static void testParse() {
        int a = 0b1100100;
        int aa = 0144;
        int aaa = 100;
        int aaaa = 0x64;
        System.out.print(a == aa && aa == aaa && aaa == aaaa);

        System.out.print(Integer.toOctalString(a));
        System.out.print(Integer.toString(aa));
        System.out.print(Integer.toHexString(aaa));
        // true14410064

        int i = 0, j = 0;
        System.out.println(i++ == i);
        System.out.println(i + j++ == 2);

        System.out.println(i + j++);
        boolean result = ++i != 0 && (i + j++ == 1 ? 1 : 0) > 0;
        System.out.println(result);
        System.out.println((i + ++j == 1 ? 1 : 0));
        System.out.println(i + ++j);
    }

    private static void testInt() {
        int a = 10;
        Integer aa = 10;
        Integer aaa = new Integer(10);
        System.out.println(a == aa);
        System.out.println(a == aaa);
        System.out.println(aa == aaa);

        Integer b = 127;
        Integer bb = 127;
        System.out.println(b == bb);

        Integer c = 128;
        Integer cc = 128;
        System.out.println(c == cc);
    }
}
