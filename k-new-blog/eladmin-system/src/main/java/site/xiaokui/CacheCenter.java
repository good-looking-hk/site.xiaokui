package site.xiaokui;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.zhengjie.modules.system.domain.User;
import me.zhengjie.modules.system.service.UserService;
import me.zhengjie.modules.system.service.dto.UserDto;
import me.zhengjie.modules.system.service.dto.UserQueryCriteria;
import org.beetl.sql.core.SQLManager;
import org.beetl.sql.core.query.Query;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import site.xiaokui.constant.RedisKey;
import site.xiaokui.service.SysBlogService;
import site.xiaokui.service.impl.SysBlogServiceImpl;
import site.xiaokui.task.BlogViewCountTask;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author HK
 * @date 2019-09-26 11:06
 */
@RequiredArgsConstructor
@Component
public class CacheCenter implements ApplicationRunner, DisposableBean {

    private final SQLManager sqlManager;

    private final SysBlogServiceImpl sysBlogServiceImpl;

    private final UserService userService;

    private final BlogViewCountTask blogViewCountTask;


    private SysConfigCache sysConfigCache;

    /**
     * 避免刷新缓存时的线程竞争
     */
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock(false);

    /**
     * Spring启动完成后，初始化缓存数据
     */
    @Override
    public void run(ApplicationArguments args) {
        // 读取系统参数配置缓存
        this.sysConfigCache = initCacheMap();
        // 将数据库数据刷到缓存
        blogViewCountTask.syncDbViewCountToRedis();
    }

    /**
     * Spring会调用java.lang.Runtime.addShutdownHook(Thread hook)注册一个钩子，在以下几种情况会主动调用：
     *  1.程序正常停止
     *  2.Reach the end of program
     *  3.System.exit
     *  4.程序异常退出
     *  5.NPE
     *  6.OutOfMemory
     *  7.受到外界影响停止
     *  8.Ctrl+C
     *  9.用户注销或者关机
     */
    @Override
    public void destroy() throws Exception {
        // 将缓存数据刷新至数据库
        blogViewCountTask.syncRedisViewCountToDb();
        // 清空博客访问量贡献黑名单
        blogViewCountTask.clearContributeBlackList();
        // 删除redis中所有缓存键
        blogViewCountTask.dealAllBlogReadsRedisKey();
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

        public boolean showProject() {
            return "true".equals(this.cacheMap.get("showProject"));
        }

        public String getNginxAccessLogPath() {
            return this.cacheMap.get("nginxAccessLogPath");
        }

        public String getNginxErrorLogPath() {
            return this.cacheMap.get("nginxErrorLogPath");
        }
    }
}
