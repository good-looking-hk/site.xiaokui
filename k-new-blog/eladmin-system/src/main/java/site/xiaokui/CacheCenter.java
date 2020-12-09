package site.xiaokui;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.zhengjie.modules.system.service.UserService;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.query.Query;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import site.xiaokui.service.SysBlogService;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author HK
 * @date 2019-09-26 11:06
 */
@RequiredArgsConstructor
@Component
public class CacheCenter implements ApplicationRunner {

    private final SQLManager sqlManager;

    private final SysBlogService sysBlogService;

    private final UserService userService;

    private SysConfigCache sysConfigCache;

    /**
     * 避免刷新缓存时的线程竞争
     */
    private final static ReadWriteLock readWriteLock = new ReentrantReadWriteLock(false);

    /**
     * Spring启动完成后，初始化缓存数据
     */
    @Override
    public void run(ApplicationArguments args) {
        this.sysConfigCache = initCacheMap();
    }

    public SysConfigCache getSysConfigCache() {
        readWriteLock.readLock().lock();
        try {
            return this.sysConfigCache;
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    public void clearSysConfigCache() {
        readWriteLock.writeLock().lock();
        try {
            this.sysConfigCache = initCacheMap();
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    private SysConfigCache initCacheMap() {
        Query<SysConfig> query = sqlManager.query(SysConfig.class);
        List<SysConfig> list = query.select();
        if (list == null) {
            throw new RuntimeException("读取系统配置失败");
        }
        Map<String, String> cacheMap = new ConcurrentHashMap<>(8);
        for (SysConfig config : list) {
            cacheMap.put(config.getKey(), config.getValue());
        }
        return new SysConfigCache(cacheMap);
    }

    public static class SysConfig {
        @Getter
        @Setter
        protected String key;
        @Getter
        @Setter
        protected String value;
    }

    public static class SysConfigCache {
        @Getter
        @Setter
        private Map<String, String> cacheMap;

        SysConfigCache(Map<String, String> cacheMap) {
            this.cacheMap = cacheMap;
        }

        public String getKey(String key) {
            return this.cacheMap.get(key);
        }

        public String getIndex() {
            return this.cacheMap.get("index");
        }

        public String getBlogIndex() {
            return this.cacheMap.get("blogIndex");
        }

        public String getCompany() {
            return this.cacheMap.get("company");
        }

        public boolean showResume() {
            return "true".equals(this.cacheMap.get("showResume"));
        }

        public boolean showAbout() {
            return "true".equals(this.cacheMap.get("showAbout"));
        }

        public String getNginxAccessLogPath() {
            return this.cacheMap.get("nginxAccessLogPath");
        }

        public String getNginxErrorLogPath() {
            return this.cacheMap.get("nginxErrorLogPath");
        }
    }
}
