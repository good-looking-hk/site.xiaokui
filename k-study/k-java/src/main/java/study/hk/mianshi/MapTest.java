package study.hk.mianshi;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author HK
 * @date 2020-05-24 09:54
 */
public class MapTest {

    public static void main(String[] args) {
        HashMap<String, String> hashMap = new HashMap<>(4);
        hashMap.put(null, null);
        System.out.println(hashMap.get(null));
        hashMap.put(null, "11");
        System.out.println(hashMap.get(null));
        System.out.println(hashMap.get("11") + "\nHashMap测试结束\n");

        ConcurrentHashMap<String, String> concurrentHashMap = new ConcurrentHashMap<>(4);
        // 下面这一行不允许
        /// concurrentHashMap.put(null, null);
        // 返回null
        System.out.println(concurrentHashMap.get("11") + "\nConcurrentHashMap测试结束\n");


        Map<String, String> map = new HashMap<>(16);
        putData(map);
        map.forEach((k, v) -> System.out.println(k + "=" + v + "，键hashcode=" + k.hashCode()));

        // 不能直接将map放入，否则就丢失测试的意义，默认放入就是安装之前的遍历熟悉
        // LinkedHashMap默认遍历是安装插入顺序，也可指定为访问顺序让内部 accessOrder 为true
        map = new LinkedHashMap<>();
        putData(map);
        map.forEach((k, v) -> System.out.println(k + "=" + v + "，键hashcode=" + k.hashCode()));

        // 按照键hashcode遍历
        map = new TreeMap<>(map);
        map.forEach((k, v) -> System.out.println(k + "=" + v + "，键hashcode=" + k.hashCode()));
    }

    private static void putData(Map<String, String> map) {
        // 造这个数据也需要花点心的
        map.put("az", "33");
        map.put("by", "11");
        map.put("cr", "22");
        map.put("cm", "00");
    }
}
