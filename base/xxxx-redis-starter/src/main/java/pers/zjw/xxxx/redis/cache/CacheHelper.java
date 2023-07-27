package pers.zjw.xxxx.redis.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Objects;
import java.util.Optional;

/**
 * CacheHelper
 *
 * @author zhangjw
 * @date 2023/01/31 17:26
 */
@Component
public class CacheHelper {
    @Autowired
    private LayeringCacheManager cacheManager;

    public <T> T getValue(String cacheName, String key, Class<T> valueType) {
        return getValue(null, cacheName, key, valueType);
    }

    public <T> T getValue(Class<?> declareClass, String cacheName, String key, Class<T> valueType) {
        String cacheFullName = (null == declareClass) ? cacheName : (declareClass.getName().hashCode() + "." + cacheName);
        Optional<Object> optional = cacheManager.get(cacheFullName, key);
        if (!optional.isPresent()) return null;
        if (optional.get().getClass().isAssignableFrom(valueType)) return (T) optional.get();
        throw new ClassCastException("can't cast " + optional.get().getClass().getName() + " to " + valueType.getName());
    }

    public <T> T put(String cacheName, String key, T value) throws CacheCreateException {
        return put(null, cacheName, key, value);
    }

    public <T> T put(Class<?> declareClass, String cacheName, String key, T value) throws CacheCreateException {
        String cacheFullName = (null == declareClass) ? cacheName : (declareClass.getName().hashCode() + "." + cacheName);
        Cache cache = cacheManager.getCache(declareClass.getName() + "." + cacheName);
        if (null == cache) throw new CacheCreateException(cacheName, key);
        cache.put(key, value);
        return value;
    }

    public <T> T putIfAbsent(String cacheName, String key, T value) throws CacheCreateException {
        return putIfAbsent(null, cacheName, key, value);
    }

    public <T> T putIfAbsent(Class<?> declareClass, String cacheName, String key, T value) throws CacheCreateException {
        Assert.notNull(value, "cache value must not empty");
        String cacheFullName = (null == declareClass) ? cacheName : (declareClass.getName().hashCode() + "." + cacheName);
        Cache cache = cacheManager.getCache(cacheName);
        if (null == cache) throw new CacheCreateException(cacheName, key);
        Cache.ValueWrapper wrapper = cache.putIfAbsent(key, value);
        if (null == wrapper || null == wrapper.get()) return value;
        if (Objects.requireNonNull(wrapper.get()).getClass().isAssignableFrom(value.getClass())) return (T) wrapper.get();
        throw new ClassCastException("can't cast " + wrapper.get().getClass().getName() + " to " + value.getClass().getName());
    }

    public void evict(String cacheName, String key) {
        evict(null, cacheName, key);
    }

    public void evict(Class<?> declareClass, String cacheName, String key) {
        String cacheFullName = (null == declareClass) ? cacheName : (declareClass.getName().hashCode() + "." + cacheName);
        Cache cache = cacheManager.get(cacheName);
        if (null == cache) return;
        cache.evict(key);
    }

    public void clear(String cacheName) {
        clear(null, cacheName);
    }

    public void clear(Class<?> declareClass, String cacheName) {
        String cacheFullName = (null == declareClass) ? cacheName : (declareClass.getName().hashCode() + "." + cacheName);
        Cache cache = cacheManager.get(cacheName);
        if (null == cache) return;
        cache.clear();
    }
}
