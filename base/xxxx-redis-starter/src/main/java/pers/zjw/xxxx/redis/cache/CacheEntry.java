package pers.zjw.xxxx.redis.cache;

import cn.hutool.core.lang.Assert;
import com.github.benmanes.caffeine.cache.Caffeine;
import pers.zjw.xxxx.foundation.toolkit.DurationParser;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * CacheItem
 *
 * @author zhangjw
 * @date 2023/01/19 0019 18:45
 */
public class CacheEntry {
    private int capacity;
    private Duration ttl;
    private String name;

    public static CacheEntry from(Map<String, Object> map) {
        Assert.notEmpty(map, "cache entry must not null");
        String name = (String)map.get("name");
        Assert.notBlank(name, "cache name must not null");
        String ttl = (String) map.get("ttl");
        String capacity = (String) map.get("capacity");
        if (StringUtils.hasText(ttl) && StringUtils.hasText(capacity)) {
            return new CacheEntry(name, ttl, Integer.parseInt(capacity));
        }
        if (StringUtils.hasText(ttl)) {
            return new CacheEntry(name, ttl);
        }
        return new CacheEntry(name);
    }

    public CacheEntry(String name) {
        this.name = name;
        this.ttl = Duration.ofSeconds(CacheEntryProvider.DEFAULT_TTL);
        this.capacity = CacheEntryProvider.DEFAULT_MAXSIZE;
    }

    public CacheEntry(String name, String ttl) {
        this.name = name;
        this.ttl = DurationParser.parse(ttl);
        this.capacity = CacheEntryProvider.DEFAULT_MAXSIZE;
    }

    public CacheEntry(String name, String ttl, int capacity) {
        this(name, ttl);
        this.capacity = capacity;
    }

    public int capacity() {
        return capacity;
    }

    public Duration ttl() {
        return ttl;
    }

    public String name() {
        return name;
    }

    public Caffeine to() {
        Caffeine caffeine = Caffeine.newBuilder();
        caffeine.recordStats().
                initialCapacity(100).
                expireAfterAccess(ttl.getSeconds(), TimeUnit.SECONDS).
                maximumSize(capacity);
        return caffeine;
    }
}
