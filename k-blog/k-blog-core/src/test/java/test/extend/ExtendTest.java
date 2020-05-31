package test.extend;

/**
 * @author HK
 * @date 2018-06-25 19:11
 */
public class ExtendTest  {

    private String str = "1";

    public ExtendTest() {
        test();
    }

    public void test() {
        System.out.println(str);
    }

    public static void main(String[] args) {
        new ExtendTest();
    }

}
