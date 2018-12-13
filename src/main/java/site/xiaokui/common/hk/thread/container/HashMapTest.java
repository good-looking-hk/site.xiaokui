package site.xiaokui.common.hk.thread.container;

import io.swagger.models.auth.In;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author HK
 * @date 2018-10-16 15:51
 */
public class HashMapTest {

    public static void main(String[] args) throws InterruptedException {
        Map<String, Integer> map = new HashMap();
        test(map);
        map = new Hashtable<>();
//        test(map);
        map = new ConcurrentHashMap();
        test(map);
    }

    private static void test(Map<String, Integer> map) {
//        map.put("1", null);
//        map.put(null, null);
        map.put(null, 1);
        System.out.println(map.get(1));
        System.out.println(map.get(null));
    }

    private static void test1() throws InterruptedException{
        final HashMap<String, String> map = new HashMap<>();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10000; i++) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            map.put(UUID.randomUUID().toString(), "");
                        }
                    }, "ftf" + i).start();
                }
            }
        }, "ftf");
        thread.start();
        thread.join();
    }
}
