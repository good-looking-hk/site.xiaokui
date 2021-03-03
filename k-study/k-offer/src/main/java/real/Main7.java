package real;

/**
 * 给定一个只含 正负数 的数字数组，输出和最大的最小子串和
 * 如 1 -2 12 -3 4 8 1 -9 2 -> 12 - 3 + 4 + 8 + 1 = 13 + 9 = 22
 * @author HK
 * @date 2021-03-01 14:49
 */
public class Main7 {

    private static int calc(int[] arr) {
        int before = 0;
        int max = 0;
        for (int i = 0; i < arr.length; i++) {
            int value = arr[i];
            // 是否需要另开一个计算窗口
            if (before + value < 0) {
                before = 0;
            } else if (value >= 0) {
                before += value;
            } else {
                before += value;
            }

            if (before > max) {
                max = before;
            }
        }
        return max;
    }

    public static void main(String[] args) {
        System.out.println(calc(new int[] {1, -2, 12, -3, 4, 8, 1, -9, 2}));
    }
}
