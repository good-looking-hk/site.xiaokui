package real;

/**
 * 将两个n位只含数字的字符串相加
 *
 * @author HK
 * @date 2020-11-11 15:13
 */
public class Main1 {

    static boolean isDebug = true;

    /**
     * 先取的作为比较基准数
     *
     * @param str1 如  789
     * @param str2 如 9876
     */
    public static String addStr(String str1, String str2) {
        String maxStr = str1.length() >= str2.length() ? str1 : str2;
        String minStr = str1 == maxStr ? str2 : str1;
        if (isDebug) {
            System.out.println("maxStr=" + maxStr + ", minStr=" + minStr);
        }
        // 进位标志
        boolean needAddOne = false;
        String targetStr = "";
        for (int i = minStr.length() - 1; i >= 0; i--) {
            // 这里转int有两种，一是以字符0作为标准，而是 Integer.parseInt()，本质都是基于第一种
            int int1 = minStr.charAt(i) - '0';
            int int2 = maxStr.charAt(i + (maxStr.length() - minStr.length())) - '0';
            int intSum = int1 + int2;
            // 上一次计算需要进位
            if (needAddOne) {
                intSum += 1;
            }
            // 需进位
            if (intSum > 9) {
                intSum -= 10;
                needAddOne = true;
            } else {
                needAddOne = false;
            }
            targetStr = intSum + targetStr;
            if (isDebug) {
                System.out.println("int1=" + int1 + ", int2=" + int2 + ", intSum=" + intSum + ", needAddOne=" + needAddOne + ", targetStr=" + targetStr);
            }
        }
        if (isDebug) {
            System.out.println("执行短位加运行后targetStr=" + targetStr + "，是否需要进位=" + needAddOne);
        }
        // 如果不需要进位, 如 1234 + 4321、123 + 1
        if (!needAddOne) {
            return maxStr.substring(0, maxStr.length() - minStr.length()) + targetStr;
        } else {
            // 如果需要进位，分两种情况，一是原两字符串长度相同，这种情况加1即可
            // 二是原长字符串个位需要加1
            if (maxStr.length() == minStr.length()) {
                return '1' + targetStr;
            } else {
                return addStr(maxStr.substring(0, maxStr.length() - minStr.length()), "1") + targetStr;
            }
        }
    }


    public static void main(String[] args) {
        isDebug = false;
        String testStr11 = "1234";
        String testStr12 = "321";
        String testStr21 = "1234";
        String testStr22 = "4321";
        String testStr31 = "4321";
        String testStr32 = "1";

        String testStr51 = "789";
        String testStr52 = "9876";
        String testStr61 = "789";
        String testStr62 = "2876";

        String testStr81 = "345678967890987";
        String testStr82 = "56789045677654321";

        String testStr91 = "9999999999999999999999999999999";
        String testStr92 = "9999999999999999999999999999999";
        String testStr93 = "99999999999999999999999999999";
        String testStr94 = "9999999999999999999999999999999";

        print(testStr11, testStr12, "测试不需要进位1");
        print(testStr21, testStr22, "测试不需要进位2");
        print(testStr31, testStr32, "测试不需要进位3");

        print(testStr51, testStr52, "测试需要进位1");
        print(testStr61, testStr62, "测试需要进位2");

        print(testStr81, testStr82, "极端测试1");
        /// print(testStr91, testStr92, "极端测试2");
        System.out.println(addStr(testStr91, testStr92));
        System.out.println(addStr(testStr93, testStr94));

        System.out.println("".substring(0, 0));
    }

    private static void print(String str1, String str2, String testMsg) {
        System.out.println(testMsg);
        String expect = String.valueOf(Long.parseLong(str1) + Long.parseLong(str2));
        String actual = addStr(str1, str2);
        System.out.println(str1 + " + " + str2 + " 期望为" + expect);
        System.out.println(str1 + " + " + str2 + " 实际为" + actual);
        System.out.println("=========================");
        if (!expect.equals(actual)) {
            throw new RuntimeException("测试失败，请检查");
        }
    }
}