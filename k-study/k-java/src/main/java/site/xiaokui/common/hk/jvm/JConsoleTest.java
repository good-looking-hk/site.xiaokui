package site.xiaokui.common.hk.jvm;


import java.util.ArrayList;
import java.util.List;

/**
 * -Xms100m -Xmx100m -XX:+UseSerialGC
 * @author HK
 * @date 2019-12-12 14:40
 */
public class JConsoleTest {

    static class OOMObject {
        public byte[] placeholder = new byte[64 * 1024];
    }

    public static void fillHeap(int num) throws InterruptedException {
        List<OOMObject> list = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            // 稍作延迟，使监视曲线的变化更加明显
            Thread.sleep(100);
            list.add(new OOMObject());
        }
        System.gc();
    }

    public static void main(String[] args) throws InterruptedException{
        Thread.sleep(5000);
        fillHeap(1000);
        Thread.sleep(5000);
    }
}
