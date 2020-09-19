package study.hk.offer.niuke;

/**
 * @author HK
 * @date 2018-12-12 20:03
 */
public class FortyNineToFiftyFour {

    /**
     * 将一个字符串转换成一个整数(实现Integer.valueOf(string)的功能，但是string不符合数字要求时返回0)，
     * 要求不能使用字符串转换整数的库函数。 数值为0或者字符串不是一个合法的数值则返回0
     */
    static class FortyNine {
        public int StrToInt(String str) {
            if (str == null || str.length() == 0) {
                return 0;
            }
            if (str.length() == 1) {
                return isNumber(str.charAt(0)) ? str.charAt(0) - (int) '0' : 0;
            }
            int symbol = 1;
            if (str.charAt(0) == '-') {
                symbol = -1;
                str = str.substring(1);
            }
            if (str.charAt(0) == '+') {
                str = str.substring(1);
            }
            int sum = 0;
            char[] chars = str.toCharArray();
            for (int i = chars.length - 1; i >= 0; i--) {
                if (!isNumber(chars[i])) {
                    return 0;
                }
                int temp = 1;
                for (int j = chars.length - 1 - i; j > 0; j--) {
                    temp *= 10;
                }
                sum += ((int) chars[i] - (int) '0') * temp;
            }
            return sum * symbol;
        }

        private boolean isNumber(char n) {
            int zero = (int) '0';
            if (n - zero < 0 || n - zero > 9) {
                return false;
            }
            return true;
        }
    }

    /**
     * 在一个长度为n的数组里的所有数字都在0到n-1的范围内。 数组中某些数字是重复的，但不知道有几个数字是重复的。
     * 也不知道每个数字重复几次。请找出数组中任意一个重复的数字。 例如，如果输入长度为7的数组{2,3,1,0,2,5,3}，那么对应的输出是第一个重复的数字2。
     */
    static class Fifty {
        public boolean duplicate(int numbers[], int length, int[] duplication) {
            int[] map = new int[length];
            for (int i = 0; i < length; i++) {
                map[numbers[i]]++;
            }
            for (int i = 0; i < length; i++) {
                if (map[numbers[i]] > 1) {
                    duplication[0] = numbers[i];
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * 给定一个数组A[0,1,...,n-1],请构建一个数组B[0,1,...,n-1],其中B中的元素B[i]=A[0]*A[1]*...*A[i-1]*A[i+1]*...*A[n-1]。不能使用除法。
     */
    static class FiftyOne {
        public int[] multiply(int[] A) {
            int length = A.length;
            int[] B = new int[length];
            if (length != 0) {
                B[0] = 1;
                //计算下三角连乘
                for (int i = 1; i < length; i++) {
                    B[i] = B[i - 1] * A[i - 1];
                }
                int temp = 1;
                //计算上三角
                for (int j = length - 2; j >= 0; j--) {
                    temp *= A[j + 1];
                    B[j] *= temp;
                }
            }
            return B;
        }
    }

    /**
     * 请实现一个函数用来匹配包括'.'和'*'的正则表达式。模式中的字符'.'表示任意一个字符，
     * 而'*'表示它前面的字符可以出现任意次（包含0次）。 在本题中，匹配是指字符串的所有字符匹配整个模式。
     * 例如，字符串"aaa"与模式"a.a"和"ab*ac*a"匹配，但是与"aa.a"和"ab*a"均不匹配
     * "" ".*","a" "a.","a" "ab*", "aa" "a*", "aaa" "a*a"
     */
    static class FiftyTwo {
        public boolean match(char[] str, char[] pattern) {
            if (str.length == 0) {
                if (pattern.length == 0) {
                    return true;
                }
                if (pattern.length == 2 && pattern[1] == '*') {
                    return true;
                }
                return false;
            }
            if (pattern.length == 0) {
                return false;
            }
            if (pattern.length == 2 && pattern[0] == '.' && pattern[1] == '*') {
                return true;
            }
            int index = 0;
            for (int i = 0; i < pattern.length - 1; i++) {
                if (pattern[i] != '.' && pattern[i] != '*' && pattern[i + 1] != '.' && pattern[i + 1] != '*') {
                    if (pattern[i] != str[index]) {
                        return false;
                    }
                    index++;
                } else if (pattern[i + 1] == '.') {
                    if (pattern[i] != str[index]) {
                        return false;
                    }
                    index += 2;
                    i++;
                } else if (pattern[i + 1] == '*') {
                    if (i + 2 == pattern.length) {
                        while (str[index] == pattern[i]) {
                            if (index == str.length - 1) {
                                return true;
                            }
                            index++;
                        }
                    }
                    if (i + 2 != pattern.length) {
                        if (str[index] == pattern[i + 2]) {
                            if (str[index] != pattern[i]) {
                                i++;
                                continue;
                            }
                            while (str[index] == pattern[i]) {
                                if (index == str.length - 1) {
                                    i++;
                                    while (++i != pattern.length) {
                                        if (pattern[i] != str[index]) {
                                            return false;
                                        }
                                    }
                                    return true;
                                }
                                index++;
                            }
                        } else {
                            if (str[index] != pattern[i]) {
                                return false;
                            } else {
                                while (str[index] == pattern[i]) {
                                    index++;
                                }
                            }
                        }
                    }
                }
                if (index > str.length - 1) {
                    if (i == pattern.length - 3 && pattern[i + 2] == '*') {
                        return true;
                    }
                    return false;
                }
            }
            if (index != str.length - 1) {
                return false;
            }
            return true;
        }


        public boolean match1(char[] str, char[] pattern) {
            if (str == null || pattern == null) {
                return false;
            }
            int strIndex = 0;
            int patternIndex = 0;
            return matchCore(str, strIndex, pattern, patternIndex);
        }

        public boolean matchCore(char[] str, int strIndex, char[] pattern, int patternIndex) {
            //有效性检验：str到尾，pattern到尾，匹配成功
            if (strIndex == str.length && patternIndex == pattern.length) {
                return true;
            }
            //pattern先到尾，匹配失败
            if (strIndex != str.length && patternIndex == pattern.length) {
                return false;
            }
            //模式第2个是*，且字符串第1个跟模式第1个匹配,分3种匹配模式；如不匹配，模式后移2位
            if (patternIndex + 1 < pattern.length && pattern[patternIndex + 1] == '*') {
                if ((strIndex != str.length && pattern[patternIndex] == str[strIndex]) || (pattern[patternIndex] == '.' && strIndex != str.length)) {
                    return matchCore(str, strIndex, pattern, patternIndex + 2)//模式后移2，视为x*匹配0个字符
                            || matchCore(str, strIndex + 1, pattern, patternIndex + 2)//视为模式匹配1个字符
                            || matchCore(str, strIndex + 1, pattern, patternIndex);//*匹配1个，再匹配str中的下一个
                } else {
                    return matchCore(str, strIndex, pattern, patternIndex + 2);
                }
            }
            //模式第2个不是*，且字符串第1个跟模式第1个匹配，则都后移1位，否则直接返回false
            if ((strIndex != str.length && pattern[patternIndex] == str[strIndex]) || (pattern[patternIndex] == '.' && strIndex != str.length)) {
                return matchCore(str, strIndex + 1, pattern, patternIndex + 1);
            }
            return false;
        }
    }

    /**
     * 请实现一个函数用来判断字符串是否表示数值（包括整数和小数）。例如，字符串"+100","5e2","-123","3.1416"和"-1E-16"都表示数值。
     * 但是"12e","1a3.14","1.2.3","+-5"和"12e+4.3"都不是。
     */
    static class FiftyThree {
        public boolean isNumeric(char[] str) {
            String s = String.valueOf(str);
            try {
                Double.parseDouble(s);
            } catch (Exception e) {
                return false;
            }
            return true;
        }
    }

    static class FiftyFour {
        //Insert one char from stringstream
        int [] book=new int[256];
        StringBuffer s=new StringBuffer();
        public void Insert(char ch)
        {
            s.append(ch);
            if(book[ch]==0){
                book[ch]=1;
            }else{
                book[ch]+=1;
            }
        }
        //return the first appearence once char in current stringstream
        public char FirstAppearingOnce()
        {
            char [] str=s.toString().toCharArray();
            for(char ch:str){
                if(book[ch]==1){
                    return ch;
                }
            }
            return '#';
        }
    }

    public static void main(String[] args) {
        System.out.println((int) '0');
        System.out.println('1');
        System.out.println('8');
        System.out.println((int) '9');
    }
}
