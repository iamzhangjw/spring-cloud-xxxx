package pers.zjw.xxxx.redis.cache;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * LayeringCacheManager
 *
 * @author zhangjw
 * @date 2023/01/20 0020 9:44
 */
public class LayeringCacheManager implements CacheManager {
    private final CacheManager localCacheManager;
    private final CacheManager remoteCacheManager;

    private final boolean useLocal;
    private final boolean useRemote;

    private final RedisTemplate<String, String> redisTemplate;

    private final Map<String, LayeringCache> cacheMap = new ConcurrentHashMap<>(1024);

    protected LayeringCacheManager(boolean useLocal, CaffeineCacheManager caffeineCacheManager,
                                   boolean useRemote, RedisCacheManager redisCacheManager,
                                   RedisTemplate<String, String> redisTemplate) {
        this.useLocal = useLocal;
        this.localCacheManager = caffeineCacheManager;
        this.useRemote = useRemote;
        this.remoteCacheManager = redisCacheManager;
        this.redisTemplate = redisTemplate;
    }

    protected Optional<Object> get(String name, Object key) {
        LayeringCache cache = get(name);
        if (null == cache) return Optional.empty();
        Object object = cache.lookup(key);
        if (null == object) return Optional.empty();
        return Optional.of(object);
    }

    protected LayeringCache get(String name) {
        return cacheMap.get(name);
    }

    @Override
    public Cache getCache(String name) {
        LayeringCache cache = cacheMap.get(name);
        if (null != cache) return cache;
        cache = createCache(name);
        LayeringCache exist = cacheMap.putIfAbsent(name, cache);
        if (null == exist) return cache;
        return exist;
    }

    @Override
    public Collection<String> getCacheNames() {
        return Collections.unmodifiableSet(this.cacheMap.keySet());
    }

    protected CacheManager getLocalCacheManager() {
        return this.localCacheManager;
    }

    protected CacheManager getRemoteCacheManager() {
        return this.remoteCacheManager;
    }

    protected LayeringCache createCache(String name) {
        return new LayeringCache(name, useLocal,
                useLocal ? (CaffeineCache) localCacheManager.getCache(name) : null,
                useRemote, useRemote ? (RedisCache) remoteCacheManager.getCache(name) : null,
                redisTemplate);
    }
}