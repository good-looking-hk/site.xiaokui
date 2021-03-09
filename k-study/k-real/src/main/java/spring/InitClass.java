package spring;

/**
 * @author HK
 * @date 2021-02-08 21:27
 */
public class InitClass {

    public static void main(String[] args){
        // 输出为 2 3 a=100, b=0 1 4
        f1();
    }

    static InitClass javaTest = new InitClass();

    static {
        System.out.print("1 ");
    }

    {
        System.out.print("2 ");
    }

    InitClass(){
        System.out.print("3 ");
        System.out.print("a=" + a + ", b=" + b + " ");
    }

    public static void f1(){
        System.out.print("4 ");
    }

    int a = 100;
    static int b = 200;
}
