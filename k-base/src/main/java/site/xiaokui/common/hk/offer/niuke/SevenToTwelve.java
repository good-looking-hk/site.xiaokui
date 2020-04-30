package site.xiaokui.common.hk.offer.niuke;

/**
 * @author HK
 * @date 2018-11-07 06:49
 */
public class SevenToTwelve {

    /**
     * 大家都知道斐波那契数列，现在要求输入一个整数n，请你输出斐波那契数列的第n项（从0开始，第0项为0）。
     * n<=39
     */
    static class Seven {

        public int Fibonacci(int n) {
            if (n == 0) {
                return 0;
            }
            if (n == 1) {
                return 1;
            }
            return Fibonacci(n - 1) + Fibonacci(n - 2);
        }

        public int Fibonacci1(int n) {
            if (n <= 0) {
                return 0;
            }
            if (n == 1) {
                return 1;
            }
            int a = 0, b = 1;
            int c = 0;
            for (int i = 2; i <= n; i++) {
                c = a + b;
                a = b;
                b = c;
            }
            return c;
        }
    }

    /**
     * 一只青蛙一次可以跳上1级台阶，也可以跳上2级。求该青蛙跳上一个n级的台阶总共有多少种跳法（先后次序不同算不同的结果）。
     */
    static class Eight {
        public int JumpFloor(int n) {
            if (n <= 0) {
                return 0;
            }
            if (n == 1) {
                return 1;
            }
            if (n == 2) {
                return 2;
            }
            int a = 1, b = 2;
            int c = 0;
            for (int i = 3; i <= n; i++) {
                c = a + b;
                a = b;
                b = c;
            }
            return c;
        }
    }

    /**
     * 一只青蛙一次可以跳上1级台阶，也可以跳上2级……它也可以跳上n级。求该青蛙跳上一个n级的台阶总共有多少种跳法。
     */
    static class Nine {
        public int JumpFloorII(int target) {
            if (target <= 0) {
                return 0;
            }
            int c = 1;
            for (int i = 1; i < target; i++) {
                c *= 2;
            }
            return c;
        }
    }

    /**
     * 我们可以用2*1的小矩形横着或者竖着去覆盖更大的矩形。请问用n个2*1的小矩形无重叠地覆盖一个2*n的大矩形，总共有多少种方法？
     */
    static class Ten {
        public int RectCover(int target) {
            if (target <= 0) {
                return 0;
            }
            if (target == 1) {
                return 1;
            }
            if (target == 2) {
                return 2;
            }
            int a = 1, b = 2, c = 0;
            for (int i = 3; i <= target; i++) {
                c = a + b;
                a = b;
                b = c;
            }
            return c;
        }
    }

    /**
     * 输入一个整数，输出该数二进制表示中1的个数。其中负数用补码表示。
     */
    static class Eleven {
        public int NumberOf1(int n) {
            int count = 0;
            String str = Integer.toBinaryString(n);
            System.out.println(str);
            for (int i = 0; i < str.length(); i++) {
                if (str.charAt(i) == '1') {
                    count++;
                }
            }
            return count;
        }

        public int NumberOf11(int n) {
            int count = 0;
            while (n != 0) {
                if ((n & 1) == 1) {
                    count++;
                }
                n = n >> 1;
            }
            return count;
        }

        private static void test() {
            int c = new Eleven().NumberOf11(100);
//            Assert.assertEquals(3, c);
//            c = new Eleven().NumberOf1(-100);
//            Assert.assertEquals(3, c);
            System.out.println(Integer.toBinaryString(100));
            System.out.println(Integer.toBinaryString(-100));
        }
    }

    /**
     * 给定一个double类型的浮点数base和int类型的整数exponent。求base的exponent次方。
     */
    static class Twelve {
        public double Power(double base, int exponent) {
            if (exponent == 0) {
                return 1;
            }
            double result = 1;
            int times = exponent > 0 ? exponent : - exponent;
            for (int i = 1; i <= times; i++) {
                result *= base;
            }
            if (exponent > 0) {
                return result;
            } else {
                return 1 / result;
            }
        }
    }

    public static void main(String[] args) {
        Eleven.test();
    }
}
