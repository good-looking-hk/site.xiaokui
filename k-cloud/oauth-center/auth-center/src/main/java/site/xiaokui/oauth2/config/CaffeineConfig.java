package site.xiaokui.oauth2.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.collect.Lists;
import lombok.Data;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import site.xiaokui.oauth2.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @date 2020-06-15 19:58
 * @author hk
 */
@Configuration
@EnableCaching
public class CaffeineConfig {

    @Bean
    public CacheManager caffeineCacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        ArrayList<CaffeineCache> caches = new ArrayList<>(4);
        List<CacheBean> list = setCacheBean();
        for(CacheBean cacheBean : list){
            caches.add(new CaffeineCache(cacheBean.getCacheName(),
                    Caffeine.newBuilder().recordStats()
                            .expireAfterWrite(cacheBean.getTtl(), TimeUnit.SECONDS)
                            .maximumSize(cacheBean.getMaximumSize())
                            .build()));
        }
        cacheManager.setCaches(caches);
        return cacheManager;
    }

    private List<CacheBean> setCacheBean(){
        List<CacheBean> list = Lists.newArrayList();
        CacheBean userCache = new CacheBean();
        userCache.setCacheName(Constants.OAUTH_CACHE_NAME);
        userCache.setTtl(Constants.OAUTH_CACHE_EXPIRED_TIME_IN_SECOND);
        userCache.setMaximumSize(10000);

        list.add(userCache);
        return list;
    }

    @Data
    private static class CacheBean {
        private String cacheName;
        private long ttl;
        private long maximumSize;
    }

}