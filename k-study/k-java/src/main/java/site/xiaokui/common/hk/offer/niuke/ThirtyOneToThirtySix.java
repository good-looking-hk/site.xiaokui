package site.xiaokui.common.hk.offer.niuke;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * @author HK
 * @date 2018-11-16 19:45
 */
public class ThirtyOneToThirtySix {

    /**
     * 输入一个整数n，求1～n这n个整数的十进制中1出现的次数。
     * 例如，输入12，1~12这些整数中包含1的数字有1,10,11和12，1一共出现了5次。
     */
    static class ThirtyOne {

        public int NumberOf1Between1AndN_Solution(int n) {
            // 1的个数
            int count = 0;
            // 当前位
            int i = 1;
            int current = 0, after = 0, before = 0;
            while ((n / i) != 0) {
                // 高位数字
                current = (n / i) % 10;
                // 当前位数字
                before = n / (i * 10);
                // 低位数字
                after = n - (n / i) * i;
                // 如果为0,出现1的次数由高位决定,等于高位数字 * 当前位数
                if (current == 0) {
                    count += before * i;
                }
                // 如果为1,出现1的次数由高位和低位决定,高位*当前位+低位+1
                else if (current == 1) {
                    count += before * i + after + 1;
                }
                // 如果大于1,出现1的次数由高位决定,//（高位数字+1）* 当前位数
                else {
                    count += (before + 1) * i;
                }
                // 前移一位
                i = i * 10;
            }
            return count;
        }
    }

    /**
     * 输入一个正整数数组，把数组里所有数字拼接起来排成一个数，打印能拼接出的所有数字中最小的一个。
     * 例如输入数组{3，32，321}，则打印出这三个数字能排成的最小数字为321323。
     */
    static class ThirtyTwo {
        public String PrintMinNumber(int[] numbers) {
            StringBuilder sb = new StringBuilder();
            ArrayList<String> list = new ArrayList<>();
            for (int n : numbers) {
                list.add(String.valueOf(n));
            }
            list.sort(new NumberComparator());
            for (String s : list) {
                sb.append(s);
            }
            return sb.toString();
        }

        static class NumberComparator implements Comparator<String> {
            @Override
            public int compare(String a, String b) {
                if (a.length() >= b.length()) {
                    for (int i = 0; i < b.length(); i++) {
                        if (a.charAt(i) > b.charAt(i)) {
                            return 1;
                        } else if (a.charAt(i) < b.charAt(i)) {
                            return -1;
                        }
                    }
                    return a.length() == b.length() ? 0 : compare(a.substring(b.length() - 1), b);
                }
                return -compare(b, a);
            }
        }
    }

    /**
     * 把只包含质因子2、3和5的数称作丑数（Ugly Number）。例如6、8都是丑数，但14不是，因为它包含质因子7。
     * 习惯上我们把1当做是第一个丑数。求按从小到大的顺序的第N个丑数
     * 1,2,3,4,5,6,8,9,10,12,15,16
     */
    static class ThirtyThree {

        public int GetUglyNumber_Solution(int index) {
            if (index <= 0) {
                return 0;
            }
            int[] result = new int[index];
            result[0] = 1;
            int i = 1, t2 = 0, t3 = 0, t5 = 0;
            while (i < index) {
                int a = result[t2] * 2;
                int b = result[t3] * 3;
                int c = result[t5] * 5;
                int min = min(a, b, c);
                result[i++] = min;
                if (min == a) {
                    t2++;
                }
                if (min == b) {
                    t3++;
                }
                if (min == c) {
                    t5++;
                }
            }
            return result[i - 1];
        }

        private int min(int a, int b, int c) {
            if (a <= b && a <= c) {
                return a;
            }
            if (b <= a && b <= c) {
                return b;
            }
            return c;
        }

        private static void test() {
            System.out.println(new ThirtyThree().GetUglyNumber_Solution(19));
            System.out.println(new ThirtyThree().GetUglyNumber_Solution1(19));
        }

        public int GetUglyNumber_Solution1(int index) {
            if (index <= 0) {
                return 0;
            }
            int count = 1;
            int i = 1;
            while (count != index) {
                if (isUgly(++i)) {
                    count++;
                }
            }
            return i;
        }

        private boolean isUgly(int n) {
            while (n % 2 == 0) {
                n /= 2;
            }
            while (n % 3 == 0) {
                n /= 3;
            }
            while (n % 5 == 0) {
                n /= 5;
            }
            return n == 1 || n == 2 || n == 3 || n == 5;
        }
    }

    /**
     * 在一个字符串(0<=字符串长度<=10000，全部由字母组成)中找到第一个只出现一次的字符,并返回它的位置,
     * 如果没有则返回 -1（需要区分大小写）
     */
    static class ThirtyFour {
        public int FirstNotRepeatingChar(String str) {
            if (str == null || str.length() == 0 || str.length() > 10000) {
                return -1;
            }
            int index = 0;
            char[] chars = new char[str.length()];
            for (int i = 0; i < str.length(); i++) {
                int j = 0;
                while (j != index) {
                    if (str.charAt(i) == str.charAt(j)) {

                    }
                }
                if (j == index) {
                    chars[++index] = 1;
                }
            }
            return index;
        }
    }


    public static void main(String[] args) {
        ThirtyThree.test();
    }
}
