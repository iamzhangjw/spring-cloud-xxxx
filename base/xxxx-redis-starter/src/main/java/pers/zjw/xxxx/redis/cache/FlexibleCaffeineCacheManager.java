package pers.zjw.xxxx.redis.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.caffeine.CaffeineCacheManager;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 支持每个缓存定制缓存规则
 *
 * @author zhangjw
 * @date 2022/07/22 0022 11:25
 *
 * reference https://www.ityuan.com/blog/1497
 */
public class FlexibleCaffeineCacheManager extends CaffeineCacheManager {
    private Map<String, Caffeine<Object, Object>> builders = new ConcurrentHashMap<>();
    private CacheLoader<Object, Object> cacheLoader;

    protected FlexibleCaffeineCacheManager () {

    }

    protected FlexibleCaffeineCacheManager(CacheEntry... entries) {
        for (CacheEntry entry : entries) {
            builders.put(entry.name(), entry.to());
        }
    }

    protected FlexibleCaffeineCacheManager(Collection<CacheEntry> entries) {
        for (CacheEntry entry : entries) {
            builders.put(entry.name(), entry.to());
        }
    }

    @Override
    protected Cache<Object, Object> createNativeCaffeineCache(String name) {
        Caffeine<Object, Object> builder = builders.get(name);
        if (builder == null) {
            return super.createNativeCaffeineCache(name);
        }
        if (this.cacheLoader != null) {
            return builder.build(this.cacheLoader);
        } else {
            return builder.build();
        }
    }

    @Override
    public void setCacheLoader(CacheLoader<Object, Object> cacheLoader) {
        this.cacheLoader = cacheLoader;
        super.setCacheLoader(cacheLoader);
    }
}
