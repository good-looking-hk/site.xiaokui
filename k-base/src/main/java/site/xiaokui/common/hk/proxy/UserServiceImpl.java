package site.xiaokui.common.hk.proxy;

/**
 * @author HK
 * @date 2020-04-11 10:45
 */
public class UserServiceImpl {

//    @Override
    public void testA() {
        System.out.println("调用testA，再调用testB");
        testB();
    }

//    @Override
    public void testB() {
        System.out.println("调用testB，再调用testC");
        testC();
    }

    public void testC() {
        System.out.println("调用testC");
    }
}
