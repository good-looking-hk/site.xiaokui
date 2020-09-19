package study.hk.offer.niuke;

import java.util.ArrayList;

/**
 * @author HK
 * @date 2018-12-12 06:31
 */
public class FortyThreeToFortyEight {

    /**
     * 对于一个给定的字符序列S，请你把其循环左移K位后的序列输出。例如，字符序列S=”abcXYZdef”,要求输出循环左移3位后的结果，即“XYZdefabc”。
     */
    static class FortyThree {
        public String LeftRotateString(String str,int n) {
            if (str == null || str.length() == 0) {
                return str;
            }
            char[] chars = str.toCharArray();
            //n = n % str.length();
            char[] temp = new char[n];
            for (int i = 0; i < n; i++) {
                temp[i] = chars[i];
            }
            for (int i = 0; i < chars.length - n; i++) {
                chars[i] = chars[i + n];
            }
            for (int i = 0; i < n; i++) {
                chars[chars.length - 1 - i]  = temp[n - 1 - i];
            }
            return String.valueOf(chars);
        }
    }

    /**
     * 例如，“student. a am I”，翻转单词顺序使其为“I am a student.”。
     */
    static class FortyFour {
        public String ReverseSentence(String str) {
            if (str == null || str.length() == 0 || !str.contains(" ")) {
                return str;
            }
            char[] chars = str.toCharArray();
            reverse(chars, 0, chars.length - 1);
            int begin = 0, end = 0;
            for (int i = 0; i < chars.length; i++) {
                if (chars[i] == ' ') {
                    end = i;
                    reverse(chars, begin, end - 1);
                    begin = end + 1;
                }
            }
            if (begin == end + 1) {
                reverse(chars, begin, chars.length - 1);
            }
            return String.valueOf(chars);
        }

        private void reverse(char[] chars, int start, int end) {
            char temp;
            while (start < end) {
                temp = chars[start];
                chars[start] = chars[end];
                chars[end] = temp;
                start++;
                end--;
            }
        }
    }

    /**
     * 大\小王可以看成任何数字,并且A看作1,J为11,Q为12,K为13，且为了方便起见,你可以认为大小王是0(有两个大小王)，判断输入的5个数是否为顺子。
     */
    static class FortyFive {
        public boolean isContinuous(int [] numbers) {
            if (numbers.length != 5) {
                return false;
            }
            quickSort(numbers, 0, numbers.length - 1);
            if (numbers[0] != 0) {
                for (int i = 0; i < numbers.length - 1; i++) {
                    if (numbers[i] + 1 != numbers[i + 1]) {
                        return false;
                    }
                }
                return true;
            } else {
                int kingCount = 0;
                for (int i = 0; i < numbers.length - 1; i++) {
                    if (numbers[i] == 0) {
                        kingCount++;
                    }
                }
                for (int i = kingCount; i < numbers.length - 1; i++) {
                    if (numbers[i] == numbers[i + 1]) {
                        return false;
                    }
                }
                int needValue = 0;
                for (int i = numbers.length - 1; i > kingCount; i--) {
                    int temp = numbers[i] - numbers[i - 1];
                    if (temp != 1) {
                        needValue += temp - 1;
                    }
                }
                if (kingCount < needValue) {
                    return false;
                } else {
                    return true;
                }
            }
        }

        private void quickSort(int[] nums, int start, int end) {
            if (start >= end || start < 0 || end > nums.length - 1) {
                return;
            }
            int index = start;
            for (int i = start; i <= end; i++) {
                if (nums[i] < nums[index]) {
                    int temp = nums[i];
                    for (int j = i; j > start; j--) {
                        nums[j] = nums[j - 1];
                    }
                    nums[start] = temp;
                    index++;
                }
            }
            quickSort(nums, start, index - 1);
            quickSort(nums, index + 1, end);
        }

        private static void test() {
            int[] arr = {5, 4, 3, 2, 1, 8, 7, 6, 9};
            new FortyFive().quickSort(arr, 0, arr.length - 1);
            for (int i : arr) {
                System.out.println(i);
            }
        }
    }

    /**
     * 0, 1, ..., n - 1这几个数字排成一个圆圈，从数字0开始，每次从这个圆圈里删除第m个数字。求出这个圆圈剩下的最后一个数字
     */
    static class FortySix {
        public int LastRemaining_Solution(int n, int m) {
            if (n <= 0 || m <= 0) {
                return -1;
            }
            ArrayList<Integer> list = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                list.add(i);
            }
            int cur = 0;
            while (list.size() != 1) {
                cur += m - 1;
                while (cur > list.size() - 1) {
                    cur = cur - list.size();
                    System.out.println("返回头坐标" + cur);
                }
                int r = list.remove(cur);
                System.out.println("移除" + r + "通过位置" + cur);
            }
            return list.get(0);
        }

        private static void test() {
            int result = new FortySix().LastRemaining_Solution(5, 3);
            System.out.println(result);
        }
    }

    /**
     * 求1+2+3+...+n，要求不能使用乘除法、for、while、if、else、switch、case等关键字及条件判断语句（A?B:C）。
     */
    static class FortySeven {
        public int Sum_Solution(int n) {
            if (n == 0) {
                return 0;
            }
            return Sum_Solution(n - 1) + n;
        }
    }

    /**
     * 写一个函数，求两个整数之和，要求在函数体内不得使用+、-、*、/四则运算符号。
     */
    static class FortyEight {
        public int Add(int num1,int num2) {
            return 0;
        }
    }

    public static void main(String[] args) {
//        FortyFive.test();
        FortySix.test();
    }
}
