package pers.zjw.xxxx.redis.cache;


import java.util.Collection;
import java.util.HashSet;

/**
 * 缓存项
 *
 * @author zhangjw
 * @date 2022/07/17 0017 10:59
 */
public class CacheEntryProvider {
    public static final int DEFAULT_MAXSIZE = 10000;
    // second
    public static final int DEFAULT_TTL = 600;

    private Collection<CacheEntry> items;

    public Collection<CacheEntry> caches() {
        return items;
    }

    public static CacheEntryProvider create() {
        CacheEntryProvider provider = new CacheEntryProvider();
        provider.items = new HashSet<>();
        return provider;
    }

    public static CacheEntryProvider create(String name) {
        CacheEntryProvider provider = create();
        provider.items.add(new CacheEntry(name));
        return provider;
    }

    public static CacheEntryProvider create(String name, String ttl) {
        CacheEntryProvider provider = create();
        provider.items.add(new CacheEntry(name, ttl));
        return provider;
    }

    public static CacheEntryProvider create(String name, String ttl, int capacity) {
        CacheEntryProvider provider = create();
        provider.items.add(new CacheEntry(name, ttl, capacity));
        return provider;
    }

    public static CacheEntryProvider create(Collection<CacheEntry> collection) {
        CacheEntryProvider provider = create();
        provider.items.addAll(collection);
        return provider;
    }

    public CacheEntryProvider with(String name) {
        this.items.add(new CacheEntry(name));
        return this;
    }

    public CacheEntryProvider with(String name, String ttl) {
        this.items.add(new CacheEntry(name, ttl));
        return this;
    }

    public CacheEntryProvider with(String name, String ttl, int capacity) {
        this.items.add(new CacheEntry(name, ttl, capacity));
        return this;
    }
}
