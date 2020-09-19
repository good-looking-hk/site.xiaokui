package study.hk.wx;

/**
 * @author HK
 * @date 2020-04-29 16:24
 */
public class Test2 {

    public static void main(String[] args) {
        method(null);
    }

    public static void method(String param) {
        switch (param) { // 肯定不是进入这里
            case "sth":
                System.out.println("it's sth");
                break;
            // 也不是进入这里
            case "null":
                System.out.println("it's null");
                break;
            // 也不是进入这里 default: System.out.println("default");
        }
    }
}
