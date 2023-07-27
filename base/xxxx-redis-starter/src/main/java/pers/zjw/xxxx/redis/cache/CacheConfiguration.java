package pers.zjw.xxxx.redis.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.annotation.AnnotationCacheOperationSource;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.interceptor.BeanFactoryCacheOperationSourceAdvisor;
import org.springframework.cache.interceptor.CacheInterceptor;
import org.springframework.cache.interceptor.CacheOperationSource;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.util.CollectionUtils;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * cache auto configuration
 *
 * @author zhangjw
 * @date 2023/01/19 0019 14:43
 */
@EnableAsync
@EnableCaching
@Configuration
public class CacheConfiguration extends CachingConfigurerSupport {
    public static final String CACHE_PREFIX = "cache:entry:";
    public static final String CACHE_EVICT = "cache:evict";

    @Bean("defaultKeyGenerator")
    public KeyGenerator keyGenerator() {
        return new DefaultKeyGenerator();
    }

    @Bean
    LayeringCacheManager layeringCacheManager(
            BeanFactoryCacheOperationSourceAdvisor cacheAdvisor, CacheInterceptor cacheInterceptor,
            RedisConnectionFactory redisConnectionFactory, CacheProperties properties,
            StringRedisSerializer stringRedisSerializer, RedisTemplate<String, String> stringRedisTemplate) {
        CacheOperationSource cacheOperationSource = new AnnotationCacheOperationSource(new ClassNameCacheAnnotationParser());
        cacheInterceptor.setCacheOperationSource(cacheOperationSource);
        cacheInterceptor.setKeyGenerator(keyGenerator());
        cacheAdvisor.setCacheOperationSource(cacheOperationSource);

        boolean useLocal = true, useRemote = true;
        CaffeineCacheManager localCacheManager = null;
        RedisCacheManager remoteCacheManager = null;
        Collection<CacheEntry> entries = Collections.emptyList();
        CacheProperties.Entry localEntry = properties.getLocal();
        if (null != localEntry) {
            useLocal = localEntry.getEnable();
            if (!CollectionUtils.isEmpty(localEntry.getEntries())) {
                entries = localEntry.getEntries().stream().map(CacheEntry::from).collect(Collectors.toList());
            }
        }
        if (useLocal) {
            localCacheManager = caffeineCacheManager(entries);
        }
        entries = Collections.emptyList();
        CacheProperties.Entry remoteEntry = properties.getRemote();
        if (null != remoteEntry) {
            useRemote = remoteEntry.getEnable();
            if (!CollectionUtils.isEmpty(remoteEntry.getEntries())) {
                entries = remoteEntry.getEntries().stream().map(CacheEntry::from).collect(Collectors.toList());
            }
        }
        if (useRemote) {
            remoteCacheManager = redisCacheManager(redisConnectionFactory, entries, stringRedisSerializer);
        }
        return new LayeringCacheManager(useLocal, localCacheManager, useRemote, remoteCacheManager, stringRedisTemplate);
    }

    @Bean
    RedisMessageListenerContainer listenerContainer(
            LayeringCacheManager layeringCacheManager, RedisConnectionFactory redisConnectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);
        container.addMessageListener(messageListener(layeringCacheManager), new PatternTopic(CacheConfiguration.CACHE_EVICT));
        return container;
    }

    private MessageListenerAdapter messageListener(LayeringCacheManager layeringCacheManager) {
        EvictLocalCacheSubscriber subscriber = new EvictLocalCacheSubscriber(layeringCacheManager);
        return new MessageListenerAdapter(subscriber);
    }

    private CaffeineCacheManager caffeineCacheManager(Collection<CacheEntry> entries) {
        CaffeineCacheManager cacheManager = CollectionUtils.isEmpty(entries)
                ? new FlexibleCaffeineCacheManager() : new FlexibleCaffeineCacheManager(entries);
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .recordStats()
                .initialCapacity(100)
                .expireAfterAccess(CacheEntryProvider.DEFAULT_TTL, TimeUnit.SECONDS)
                .maximumSize(CacheEntryProvider.DEFAULT_MAXSIZE));
        return cacheManager;
    }

    private RedisCacheManager redisCacheManager(
            RedisConnectionFactory redisConnectionFactory, Collection<CacheEntry> entries, StringRedisSerializer stringRedisSerializer) {
        RedisCacheWriter cacheWriter = new DefaultRedisCacheWriter(redisConnectionFactory);
        RedisCacheConfiguration defaultCacheConfiguration = cacheConfiguration(Duration.ofHours(2), stringRedisSerializer);
        if (!CollectionUtils.isEmpty(entries)) {
            Map<String, RedisCacheConfiguration> initialCacheConfigurations = entries.stream()
                    .collect(Collectors.toMap(CacheEntry::name, e -> cacheConfiguration(e.ttl(), stringRedisSerializer)));
            return new RedisCacheManager(cacheWriter, defaultCacheConfiguration, initialCacheConfigurations, true);
        }
        return new RedisCacheManager(cacheWriter, defaultCacheConfiguration, true);
    }

    private RedisCacheConfiguration cacheConfiguration(Duration ttl, StringRedisSerializer stringRedisSerializer) {
        DefaultFormattingConversionService conversionService = new DefaultFormattingConversionService();
        RedisCacheConfiguration.registerDefaultConverters(conversionService);
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(ttl)
                .prefixCacheNameWith(CACHE_PREFIX)
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(stringRedisSerializer))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.java(null)));
    }
}
