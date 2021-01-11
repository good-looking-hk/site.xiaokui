package first;

import java.util.Arrays;

/**
 * \\ 反斜杠
 * \t 间隔 ('\u0009')
 * \n 换行 ('\u000A')
 * \r 回车 ('\u000D')
 * \d 数字 等价于 [0-9]
 * \D 非数字 等价于 [^0-9]
 * \s 空白符号 [\t\n\x0B\f\r]
 * \S 非空白符号 [^\t\n\x0B\f\r]
 * \w 单独字符 [a-zA-Z_0-9]
 * \W 非单独字符 [^a-zA-Z_0-9]
 * \f 换页符
 * \e Escape
 * \b 一个单词的边界
 * \B 一个非单词的边界
 * \G 前一个匹配的结束
 * \\+ 表示一个以上字符
 * @author HK
 * @date 2021-01-08 10:34
 */
public class TestSplit {

    public static void main(String[] args) {
        String str1 = "aa bb cc dd ee";
        convert(str1);

        str1 = "a b c aa bb cc";
        convert(str1);

        str1 = "a   b c aa   bb cc  d dd  ddd    dddd";
        convert(str1);
    }

    private static void convert(String str1) {
        System.out.println(Arrays.toString(str1.split(" ")));
        System.out.println(Arrays.toString(str1.split("\\s")));
        System.out.println(Arrays.toString(str1.split("\\W")));
        System.out.println(Arrays.toString(str1.split("\\s+")));
        System.out.println(Arrays.toString(str1.split("\\W+")));
    }
}
