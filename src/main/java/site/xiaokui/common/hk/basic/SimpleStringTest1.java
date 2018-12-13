package site.xiaokui.common.hk.basic;

/**
 * @author HK
 * @date 2018-10-08 11:29
 */
public class SimpleStringTest1 {

    public static void main(String[] args) {
        String test = "aa";
        test = test + "bb";
        System.out.println(test);

        StringBuilder test1 = new StringBuilder();
        test1.append("aa");
        test1.append("bb");
        String temp = test1.toString();
        System.out.println(temp);
    }
}
