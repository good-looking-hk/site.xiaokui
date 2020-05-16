package site.xiaokui.test;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import site.xiaokui.module.user.entity.SysUser;

import java.net.URL;

/**
 * @author HK
 * @date 2018-06-05 16:05
 */
public class EhcacheTest {

    public static void main(String[] args) {
        URL url = EhcacheTest.class.getClassLoader().getResource("ehcache.xml");
        // 1. 创建缓存管理器
        CacheManager cacheManager = CacheManager.create(url);
        // 2. 获取缓存对象
        Cache cache = cacheManager.getCache("shiro");
        // 3. 创建元素
        Element element = new Element("key1", "value1");
        // 4. 将元素添加到缓存
        cache.put(element);
        // 5. 获取缓存
        Element value = cache.get("key1");
        System.out.println(value);
        System.out.println(value.getObjectValue());
        // 6. 删除元素
        cache.remove("key1");

        SysUser user = new SysUser();
        user.setId(1);
        user.setName("11");
        Element element2 = new Element("user", user);
        cache.put(element2);

        Element value2 = cache.get("user");
        SysUser dog2 = (SysUser) value2.getObjectValue();
        System.out.println(dog2);

        System.out.println(cache.getSize());
        // 7. 刷新缓存
        cache.flush();
        // 8. 关闭缓存管理器
        cacheManager.shutdown();
    }
}
