package study.hk.basic;

/**
 * 比较几种字符串连接效率
 * @author HK
 * @date 2018-10-06 16:40
 */
public class StringAppend {

    private static int[] times = {10, 100, 1000, 10000, 100000, 200000};

    public static void main(String[] args) {
        testString();
        testStringBuffer();
        testStringBuilder();
    }

    private static void testString() {
        long start = System.currentTimeMillis();
        String test = "a";
        for (int i = 0; i < times.length; i++) {
            for (int j = 0; j < times[i]; j++) {
                test = test + "a";
            }
            System.out.println("testString:次数为" + times[i] + "，执行耗时" + (System.currentTimeMillis() - start) + "ms");
        }
        System.out.println();
    }

    private static void testStringBuffer() {
        long start = System.currentTimeMillis();
        StringBuffer buffer = new StringBuffer("a");
        for (int i = 0; i < times.length; i++) {
            for (int j = 0; j < times[i]; j++) {
                buffer.append("a");
            }
            System.out.println("testStringBuffer:次数为" + times[i] + "，执行耗时" + (System.currentTimeMillis() - start) + "ms");
        }
        System.out.println();
    }

    /**
     * 顶呱呱
     */
    private static void testStringBuilder() {
        long start = System.currentTimeMillis();
        StringBuilder builder = new StringBuilder("a");
        for (int i = 0; i < times.length; i++) {
            for (int j = 0; j < times[i]; j++) {
                builder.append("a");
            }
            System.out.println("testStringBuilder:次数为" + times[i] + "，执行耗时" + (System.currentTimeMillis() - start) + "ms");
        }
        System.out.println();
    }
}
