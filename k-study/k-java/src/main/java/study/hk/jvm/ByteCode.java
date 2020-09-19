package study.hk.jvm;

/**
 * @author HK
 * @date 2018-12-28 21:41
 */
public class ByteCode {

    private int m;

    private int inc() {
        return m + 1;
    }

    private int add(int a, int b) {
        return a + b;
    }

    private static int add1(int a, int b) {
        return a + b;
    }

    public int inc1() {
        int x;
        try {
            x = 1;
            return x;
        } catch (Exception e) {
            x = 2;
            return x;
        } finally {
            x = 3;
        }
    }
}
