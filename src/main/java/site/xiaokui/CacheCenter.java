package site.xiaokui;

import lombok.Getter;
import lombok.Setter;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import site.xiaokui.module.base.entity.SysConfig;
import site.xiaokui.module.sys.blog.util.BlogUtil;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author HK
 * @date 2019-09-26 11:06
 */
@Component
public class CacheCenter implements ApplicationRunner {

    @Autowired
    private SQLManager sqlManager;

    private SysConfigCache sysConfigCache;

    @Override
    public void run(ApplicationArguments args) {
        this.sysConfigCache = initCacheMap();
    }

    public synchronized SysConfigCache getSysConfigCache() {
        return this.sysConfigCache;
    }

    public synchronized void clearSysConfigCache() {
        this.clearBlogCache();
        this.sysConfigCache = initCacheMap();
    }

    public void clearBlogCache() {
        BlogUtil.clearBlogCache(null);
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

    public static class SysConfigCache {
        @Getter@Setter
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
    }
}
