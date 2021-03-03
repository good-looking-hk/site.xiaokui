package real;

/**
 * 给定一个字符串，输出不含有重复字符的最长子串的长度
 * 如 abcdefggasdf12345677 -> gasdf1234567   123443210 -> 43210
 * @author HK
 * @date 2021-03-01 13:49
 */
public class Main6 {

    private static String calc(String str) {
        StringBuilder before = new StringBuilder("");
        StringBuilder max = before;
        for (char c : str.toCharArray()) {
            // 如果不包含
            if (before.indexOf(String.valueOf(c)) < 0) {
                before.append(c);
            } else {
                // 发生了不包含情况，判断max是否需要发生变化
                if (max.length() < before.length()) {
                    max = before;
                }
                // 发生了包含情况，需要另开一个字符串来存储，通过比较前后得到最大
                before = new StringBuilder();
                before.append(c);
            }
        }
        // 再处理最后一种情况: 123443210
        return before.length() > max.length() ? before.toString() : max.toString();
    }

    public static void main(String[] args) {
        String str = calc("abcdefggasdf12345677");
        System.out.println(str);

        String str1 = calc("123443210");
        System.out.println(str1);
    }
}
