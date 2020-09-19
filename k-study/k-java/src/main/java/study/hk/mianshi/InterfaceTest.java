package study.hk.mianshi;

/**
 * @author HK
 * @date 2020-05-24 08:59
 */
public class InterfaceTest {

    interface Inter {
        void print();
        static void print1() {
            // 静态方法用于测试子类继承的是哪一个父接口的
            System.out.println("I am Inter print1");
        }
    }

    interface Inter1 {
        static void print1() {
            // 静态方法用于测试子类继承的是哪一个父接口的
            System.out.println("I am Inter1 print1");
        }
    }

    static class InterImp implements Inter, Inter1 {
        @Override
        public void print() {
            System.out.println("I am InterImp print");
        }
//        void print2() {
//            // 通过观察字节码，可以发现虚拟机只会为重载方法/构造方法生成方法签名
//            // 3个print2方法互为重载关系，而上面那个方法由于覆盖了父类方法，所以都会生成方法签名
//            System.out.println("I am InterImp print2");
//        }

        void noSignatureMethod(){}

        String print2() {
            // 这个方法不能够被声明，因为同一个类中不能存在方法签名相同的两个方法
            // 与上面方法相比，仅类型不同，而方法签名与上面相同
            return null;
        }

        String print2(int i, String s) {
            return null;
        }

        String print2(String s, int i) {
            return null;
        }
    }

    public static void main(String[] args) {
        InterImp interImp = new InterImp();
        // 可以调用print、print2方法，但不能调用print1
        // 所以接口中的静态方法不能被继承/实现
        interImp.print();
        interImp.print2();

        Father father = new Son();
        // 这里再扩展一下，父类的静态方法可以被重写吗？或者说可以被覆盖吗?
        // 答：静态方法没有重写这个说法，覆盖是可以的，因为静态方法绑定于类，跟对象实例无关，例如下面这个例子
        // I am father
        Father.print();
        Son son = (Son) father;
        // I am son
        Son.print();
    }
}

class Father {
    static void print() {
        System.out.println("I am father");
    }
}

class Son extends Father {
    static void print() {
        System.out.println("I am son");
    }
}