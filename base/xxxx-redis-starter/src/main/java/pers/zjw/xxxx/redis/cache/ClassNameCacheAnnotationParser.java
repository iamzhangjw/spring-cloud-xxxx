package pers.zjw.xxxx.redis.cache;

import org.springframework.cache.annotation.SpringCacheAnnotationParser;
import org.springframework.cache.interceptor.CacheEvictOperation;
import org.springframework.cache.interceptor.CacheOperation;
import org.springframework.cache.interceptor.CachePutOperation;
import org.springframework.cache.interceptor.CacheableOperation;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * ClassNameCacheAnnotationParser
 *
 * @author zhangjw
 * @date 2023/02/21 17:55
 */
public class ClassNameCacheAnnotationParser extends SpringCacheAnnotationParser {
    private static final long serialVersionUID = -5581318235871378326L;

    private static final String KEY_GENERATOR = "defaultKeyGenerator";

    @Override
    public Collection<CacheOperation> parseCacheAnnotations(Class<?> type) {
        Collection<CacheOperation> operations = super.parseCacheAnnotations(type);
        if (CollectionUtils.isEmpty(operations)) return operations;
        String cacheNamePrefix = type.getSimpleName();
        return operations.stream().map(c -> {
            if (c instanceof CacheableOperation) return parseCacheable((CacheableOperation) c, cacheNamePrefix);
            if (c instanceof CacheEvictOperation) return parseEvictAnnotation((CacheEvictOperation) c, cacheNamePrefix);
            if (c instanceof CachePutOperation) return parsePutAnnotation((CachePutOperation) c, cacheNamePrefix);
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    @Override
    public Collection<CacheOperation> parseCacheAnnotations(Method method) {
        Collection<CacheOperation> operations = super.parseCacheAnnotations(method);
        if (CollectionUtils.isEmpty(operations)) return operations;
        String cacheNamePrefix = method.getDeclaringClass().getSimpleName();
        return operations.stream().map(c -> {
            if (c instanceof CacheableOperation) return parseCacheable((CacheableOperation) c, cacheNamePrefix);
            if (c instanceof CacheEvictOperation) return parseEvictAnnotation((CacheEvictOperation) c, cacheNamePrefix);
            if (c instanceof CachePutOperation) return parsePutAnnotation((CachePutOperation) c, cacheNamePrefix);
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    private CacheableOperation parseCacheable(CacheableOperation operation, String prefix) {
        CacheableOperation.Builder builder = new CacheableOperation.Builder();
        builder.setName(operation.getName());
        builder.setCacheNames(operation.getCacheNames().stream().map(e -> {
            if (e.startsWith(prefix)) return e;
            return prefix + "." + e;
        }).toArray(String[]::new));
        builder.setCondition(operation.getCondition());
        builder.setUnless(operation.getUnless());
        builder.setKey(operation.getKey());
        builder.setKeyGenerator(StringUtils.isEmpty(operation.getKeyGenerator()) ? KEY_GENERATOR : operation.getKeyGenerator());
        builder.setCacheManager(operation.getCacheManager());
        builder.setCacheResolver(operation.getCacheResolver());
        builder.setSync(operation.isSync());
        CacheOperation target = builder.build();
        System.out.println(target.getKey() + " " + target.getKeyGenerator());
        return builder.build();
    }

    private CacheEvictOperation parseEvictAnnotation(CacheEvictOperation operation, String prefix) {
        org.springframework.cache.interceptor.CacheEvictOperation.Builder builder = new org.springframework.cache.interceptor.CacheEvictOperation.Builder();
        builder.setName(operation.getName());
        builder.setCacheNames(operation.getCacheNames().stream().map(e -> {
            if (e.startsWith(prefix)) return e;
            return prefix + "." + e;
        }).toArray(String[]::new));
        builder.setCondition(operation.getCondition());
        builder.setKey(operation.getKey());
        builder.setKeyGenerator(StringUtils.isEmpty(operation.getKeyGenerator()) ? KEY_GENERATOR : operation.getKeyGenerator());
        builder.setCacheManager(operation.getCacheManager());
        builder.setCacheResolver(operation.getCacheResolver());
        builder.setCacheWide(operation.isCacheWide());
        builder.setBeforeInvocation(operation.isBeforeInvocation());
        CacheOperation target = builder.build();
        System.out.println(target.getKey() + " " + target.getKeyGenerator());
        return builder.build();
    }

    private CacheOperation parsePutAnnotation(CachePutOperation operation, String prefix) {
        org.springframework.cache.interceptor.CachePutOperation.Builder builder = new org.springframework.cache.interceptor.CachePutOperation.Builder();
        builder.setName(operation.getName());
        builder.setCacheNames(operation.getCacheNames().stream().map(e -> {
            if (e.startsWith(prefix)) return e;
            return prefix + "." + e;
        }).toArray(String[]::new));
        builder.setCondition(operation.getCondition());
        builder.setUnless(operation.getUnless());
        builder.setKey(operation.getKey());
        builder.setKeyGenerator(StringUtils.isEmpty(operation.getKeyGenerator()) ? KEY_GENERATOR : operation.getKeyGenerator());
        builder.setCacheManager(operation.getCacheManager());
        builder.setCacheResolver(operation.getCacheResolver());
        CacheOperation target = builder.build();
        System.out.println(target.getKey() + " " + target.getKeyGenerator());
        return builder.build();
    }
}
