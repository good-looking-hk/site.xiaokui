package study.hk.basic;

/**
 * @author HK
 * @date 2018-11-04 15:18
 */
public class InnerClassTest {

    public void test () {
        class TempClass {
        }
    }

    public void testAnno() {
        new Thread(new Runnable() {
            @Override
            public void run() {
            }
        });
    }

    static class StaticClass {
    }

    abstract class MemberlClass {
        public void test(){
        }
    }

    public static void main(String[] args) {
        new InnerClassTest().test();
    }
}
