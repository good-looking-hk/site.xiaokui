package site.xiaokui.common.hk.thread.basic;

/**
 * @author HK
 * @date 2019-10-11 09:06
 */
public class Joined {

    public static void main(String[] args) throws InterruptedException {
        System.out.println(1111);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println(2222);
            }
        });
//        thread.start();
        thread.join();
        System.out.println(3333);
    }
}
