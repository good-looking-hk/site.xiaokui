package test.extend;

/**
 * @author HK
 * @date 2018-06-25 19:32
 */
public class Derive extends Base {

    private String name = "dervied";

    public Derive() {
        tellName();
        printName();
    }

    @Override
    public void tellName() {
        System.out.println("Dervied tell name: " + name);
    }

    @Override
    public void printName() {
        System.out.println("Dervied print name: " + name);
    }

    public static void main(String[] args) {
        Derive aa = new Derive();
    }
}
