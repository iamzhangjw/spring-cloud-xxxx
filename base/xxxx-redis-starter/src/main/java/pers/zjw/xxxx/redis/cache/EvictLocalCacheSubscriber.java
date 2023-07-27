package pers.zjw.xxxx.redis.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

/**
 * EvictLocalCacheSubscriber
 *
 * @author zhangjw
 * @date 2023/01/20 0020 17:42
 */
@Slf4j
public class EvictLocalCacheSubscriber implements MessageListener {
    private LayeringCacheManager cacheManager;

    protected EvictLocalCacheSubscriber(LayeringCacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Override
    public void onMessage(Message message, byte[] bytes) {
        String channel = new String(message.getChannel());
        String body = new String(message.getBody());
        log.info("receive msg {} from redis channel {}", body, channel);
        if (CacheConfiguration.CACHE_EVICT.equals(channel)) {
            CacheManager localCacheManager = cacheManager.getLocalCacheManager();
            if (null == localCacheManager) return;
            String name, key = null;
            if (body.contains(",")) {
                name = body.substring(0, body.indexOf(","));
                key = body.substring(body.indexOf(",")+1);
            } else {
                name = body;
            }
            Cache cache = localCacheManager.getCache(name);
            if (null == cache) return;
            if (null != key) {
                cache.evict(key);
            } else {
                cache.clear();
            }
        }
    }
}