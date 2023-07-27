package pers.zjw.xxxx.redis.cache;

import pers.zjw.xxxx.foundation.json.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.AbstractValueAdaptingCache;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Objects;
import java.util.concurrent.Callable;

/**
 * LayeringCache
 *
 * @author zhangjw
 * @date 2023/01/19 0019 19:20
 */
@Slf4j
public class LayeringCache extends AbstractValueAdaptingCache {
    private final String name;

    private final boolean useLocal;
    private final boolean useRemote;

    private final Cache localCache;
    private final Cache remoteCache;

    private final RedisTemplate<String, String> redisTemplate;

    protected LayeringCache(String name, boolean useLocal, CaffeineCache localCache,
                            boolean useRemote, RedisCache remoteCache,
                            RedisTemplate<String, String> redisTemplate) {
        super(true);
        this.name = name;
        this.useLocal = useLocal;
        this.useRemote = useRemote;
        this.localCache  = localCache;
        this.remoteCache = remoteCache;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public ValueWrapper get(Object key) {
        ValueWrapper value = null;
        if (useLocal) {
            value = localCache.get(key);
            log.debug("get(Object key) get cache from local, name:{}, key:{}, value:{}",
                    getName(), key, Objects.isNull(value) ? null : JsonParser.toString(value.get()));
        }
        if (useRemote && Objects.isNull(value)) {
            value = remoteCache.get(key);
            log.debug("get(Object key) get cache from remote, name:{}, key:{}, value:{}",
                    getName(), key, Objects.isNull(value) ? null : JsonParser.toString(value.get()));
            if (Objects.nonNull(value)) {
                localCache.put(key, value.get());
            }
        }
        return value;
    }

    @Override
    public <T> T get(Object key, Class<T> type) {
        T value = null;
        if (useLocal) {
            value = localCache.get(key, type);
            log.debug("get(Object key, Class<T> type) get cache from local, name:{}, key:{}, value:{}",
                    getName(), key, value);
        }
        if (useRemote && Objects.isNull(value)) {
            value = remoteCache.get(key, type);
            log.debug("get(Object key, Class<T> type) get cache from remote, name:{}, key:{}, value:{}",
                    getName(), key, value);
            if (Objects.nonNull(value)) {
                localCache.put(key, value);
            }
        }
        return value;
    }

    @Override
    protected Object lookup(Object key) {
        ValueWrapper value = null;
        if (useLocal) {
            value = localCache.get(key);
            log.debug("lookup(Object key) get cache from local, name:{}, key:{}, value:{}",
                    getName(), key, Objects.isNull(value) ? null : JsonParser.toString(value.get()));
        }
        if (useRemote && Objects.isNull(value)) {
            value = remoteCache.get(key);
            log.debug("lookup(Object key) get cache from remote, name:{}, key:{}, value:{}",
                    getName(), key, JsonParser.toString(value));
            if (Objects.nonNull(value)) {
                localCache.put(key, value.get());
            }
        }
        return Objects.isNull(value) ? null : value.get();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Object getNativeCache() {
        return this;
    }

    @Override
    public <T> T get(Object key, Callable<T> valueLoader) {
        T value = null;
        if (useLocal) {
            value = localCache.get(key, valueLoader);
            log.debug("get(Object key, Callable<T> valueLoader) get cache from local, name:{}, key:{}, value:{}",
                    getName(), key, value);
        }
        if (useRemote && Objects.isNull(value)) {
            value = remoteCache.get(key, valueLoader);
            log.debug("get(Object key, Callable<T> valueLoader) get cache from remote, name:{}, key:{}, value:{}",
                    getName(), key, value);
            if (Objects.nonNull(value)) {
                localCache.put(key, value);
            }
        }
        return value;
    }

    @Override
    public void put(Object key, Object value) {
        if (useLocal) {
            log.debug("put(Object key, Object value) put cache into local, name:{}, key:{}, value:{}",
                    getName(), key, value);
            localCache.put(key, value);
        }
        if (useRemote) {
            log.debug("put(Object key, Object value) put cache into remote, name:{}, key:{}, value:{}",
                    getName(), key, value);
            remoteCache.put(key, value);
        }
    }

    @Override
    public ValueWrapper putIfAbsent(Object key, Object value) {
        ValueWrapper wrapper = null;
        if (useLocal) {
            log.debug("putIfAbsent(Object key, Object value) put cache into local, name:{}, key:{}, value:{}",
                    getName(), key, value);
            wrapper = localCache.putIfAbsent(key, value);
        }
        if (useRemote) {
            log.debug("putIfAbsent(Object key, Object value) put cache into local, name:{}, key:{}, value:{}",
                    getName(), key, value);
            wrapper = remoteCache.putIfAbsent(key, value);
        }
        return wrapper;
    }

    @Override
    public void evict(Object key) {
        log.debug("evict cache key {} in {}", getName(), key);
        if (useLocal) {
            localCache.evict(key);
            log.debug("send evict cache msg to queue {}", CacheConfiguration.CACHE_EVICT);
            redisTemplate.convertAndSend(CacheConfiguration.CACHE_EVICT, this.name + "," + JsonParser.toString(key));
        }
        if (useRemote) {
            log.debug("remove remote cache key {} in {}", key, getName());
            remoteCache.evict(key);
        }
    }

    @Override
    public void clear() {
        log.debug("remove cache {}", getName());
        if (useLocal) {
            localCache.clear();
            log.debug("send clear cache msg to queue {}", CacheConfiguration.CACHE_EVICT);
            redisTemplate.convertAndSend(CacheConfiguration.CACHE_EVICT, this.name);
        }
        if (useRemote) {
            log.debug("clear remote cache name {}", getName());
            remoteCache.clear();
        }
    }
}
