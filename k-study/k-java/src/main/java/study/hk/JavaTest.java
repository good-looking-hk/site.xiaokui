package study.hk;

/**
 * @author HK
 * @date 2020-04-09 17:31
 */
public class JavaTest {

    public static void main(String[] args){
        f1();
    }

    static JavaTest javaTest = new JavaTest();

    static {
        System.out.print("1 ");
    }

    {
        System.out.print("2 ");
    }

    JavaTest(){
        System.out.print("3 ");
        System.out.print("a=" + a + ", b=" + b + " ");
    }

    public static void f1(){
        System.out.print("4 ");
    }

    int a = 100;
    static int b = 200;
}
