package pers.zjw.xxxx.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Redis operations service that all keys/hashKeys are String. By default, Object value
 * serialized by jdkSerializer while primitive or boxing wrapper value represented in String
 * furthermore, json format is also supported.
 *
 */
@Slf4j
@Service
@SuppressWarnings("unchecked")
public class RedisService {
    private final int DEFAULT_EXPIRATION_MINUTES = 30;

    @Autowired
    private RedisTemplate<String, Object> objValueRedisTemplate;

    @Autowired
    private RedisTemplate<String, String> stringRedisTemplate;

    @Autowired
    private RedisTemplate<String, Object> jsonValueRedisTemplate;

    /**
     * Get object according to class type and return the value. It uses clazz argument to
     * determine deserializer method as a consideration of compatible with media-module.
     * It assumes users know the real type of stored one.
     * Not suitable for String and primitive wrapper class type, eg. Integer, Char...
     * If the type not clear, simply given 'Object' will be OK
     *
     * @param key   String
     * @param clazz class of the value
     * @return T instance of clazz
     * @see RedisService#getString
     */
    public <T> T getObject(String key, Class<T> clazz) {
        Assert.hasText(key, "key must not be blank");
        Assert.notNull(clazz, "clazz must not be null");
        if (ClassUtils.isPrimitiveOrWrapper(clazz) || clazz == String.class) {
            throw new IllegalArgumentException("not supported for primitive, wrapper and String");
        }

        Object obj = objValueRedisTemplate.opsForValue().get(key);
        return obj == null ? null : (T) obj;
    }

    /**
     * get primitive or boxing wrapper values or String and return String
     *
     * @param key String
     * @return value String
     */
    public String getString(String key) {
        Assert.hasText(key, "key must not be blank");
        return stringRedisTemplate.opsForValue().get(key);
    }

    public Boolean hasKey(String key) {
        Assert.hasText(key, "key must not be blank");
        return stringRedisTemplate.hasKey(key);
    }

    /**
     * batch fetch values by multiple keys, this method use jdkDeserializer
     * Not suitable for primitive, wrapper class type and String
     *
     * @param keys List<Object>
     * @return Object
     * @see RedisService#multiGetStrings
     */
    public List<Object> multiGetObjects(List<String> keys) {
        Assert.notEmpty(keys, "keys are empty");
        return objValueRedisTemplate.opsForValue().multiGet(keys);
    }

    public List<String> multiGetStrings(List<String> keys) {
        Assert.notEmpty(keys, "keys are empty");
        return stringRedisTemplate.opsForValue().multiGet(keys);
    }

    /**
     * delete key and return a boolean result that the caller must determine
     *
     * @param key String
     * @return boolean that dedicates success or not
     */
    public void delete(String key) {
        Assert.hasText(key, "key must not be blank");
        objValueRedisTemplate.delete(key);
    }

    /**
     * expire key with minutes and return a boolean result that the caller must determine
     *
     * @param key String
     * @return boolean that dedicates success or not
     */
    public Boolean expire(String key, int minToLive) {
        Assert.hasText(key, "key must not be blank");
        return objValueRedisTemplate.expire(key, minToLive, TimeUnit.MINUTES);
    }

    /**
     * set {@code <key, val> } without timeout
     *
     * @param key              redis key
     * @param value            redis value
     *
     */
    public void persist(String key, Object value) {
        Assert.hasText(key, "key must not be blank");
        Assert.notNull(value, "value must not be null");
        if (ClassUtils.isPrimitiveOrWrapper(value.getClass()) || value.getClass() == String.class) {
            stringRedisTemplate.opsForValue().set(key, String.valueOf(value));
        } else {
            objValueRedisTemplate.opsForValue().set(key, value);
        }
    }

    /**
     * set {@code <key, val> } with default minutes expiration
     * {@link RedisService#DEFAULT_EXPIRATION_MINUTES}.
     *
     * @param key   String
     * @param value Object
     * @see RedisService#DEFAULT_EXPIRATION_MINUTES
     */
    public void set(String key, Object value) {
        Assert.hasText(key, "key must not be blank");
        Assert.notNull(value, "value must not be null");
        doSet(key, value, DEFAULT_EXPIRATION_MINUTES);
    }

    /**
     * set {@code <key, val> } with {@code minToLive} minutes expiration
     *
     * @param key       String
     * @param value     Object
     * @param minToLive expiration minutes
     */
    public void set(String key, Object value, int minToLive) {
        Assert.hasText(key, "key must not be blank");
        Assert.notNull(value, "value must not be null");
        doSet(key, value, minToLive);
    }

    private void doSet(String key, Object value, int minToLive) {
        if (ClassUtils.isPrimitiveOrWrapper(value.getClass()) || value.getClass() == String.class) {
            stringRedisTemplate.opsForValue().set(key, String.valueOf(value),
                    minToLive, TimeUnit.MINUTES);
        } else {
            objValueRedisTemplate.opsForValue().set(key, value, minToLive, TimeUnit.MINUTES);
        }
    }

    /**
     * set {@code <key, val> } with json serializer with default minutes expiration
     *
     * @param key   String
     * @param value Object
     * @see RedisService#DEFAULT_EXPIRATION_MINUTES
     */
    public void setByJson(String key, Object value) {
        Assert.hasText(key, "key must not be blank");
        Assert.notNull(value, "value must not be null");
        jsonValueRedisTemplate.opsForValue().set(key, value, DEFAULT_EXPIRATION_MINUTES,
                TimeUnit.MINUTES);
    }

    /**
     * get with json serializer
     *
     * @param key   String
     * @param clazz class of the value
     * @return T instance of clazz
     */
    public <T> T getByJson(String key, Class<T> clazz) {
        Assert.hasText(key, "key must not be blank");
        Assert.notNull(clazz, "clazz must not be null");
        Object obj = jsonValueRedisTemplate.opsForValue().get(key);
        return obj == null ? null : (T) obj;
    }

    /**
     * set {@code <key, val> } if absent with {@code minToLive} minutes expiration. The method
     * return a boolean result that the caller must determine
     *
     * @param key       String
     * @param value     Object
     * @param minToLive expiration minutes
     * @return boolean that dedicates success or not
     */
    public Boolean setIfAbsent(String key, Object value, int minToLive) {
        Assert.hasText(key, "key must not be blank");
        Assert.notNull(value, "value must not be null");
        boolean result;
        if (ClassUtils.isPrimitiveOrWrapper(value.getClass()) || value.getClass() == String.class) {
            result = stringRedisTemplate.opsForValue().setIfAbsent(key, String.valueOf(value));
        } else {
            result = objValueRedisTemplate.opsForValue().setIfAbsent(key, value);
        }
        return result && expire(key, minToLive);
    }

    /**
     * @param key   String
     * @param delta int
     * @return long – the value after increment
     */
    public Long increment(String key, int delta) {
        Assert.hasText(key, "key must not be blank");
        return stringRedisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * Add {@code <hashKey, val> } into hash
     *
     * @param key     String
     * @param hashKey String
     *
     */
    public void hashSet(String key, String hashKey, Object value) {
        Assert.hasText(key, "key must not be blank");
        Assert.hasText(hashKey, "hashKey must not be blank");
        Assert.notNull(value, "value must not be blank");
        if (value.getClass() == String.class) {
            stringRedisTemplate.opsForHash().put(key, hashKey, String.valueOf(value));
        } else {
            objValueRedisTemplate.opsForHash().put(key, hashKey, value);
        }
    }

    public void hashIncrementBy(String key, String hashKey, int delta) {
        Assert.hasText(key, "key must not be blank");
        Assert.hasText(hashKey, "hashKey must not be blank");
        objValueRedisTemplate.opsForHash().increment(key, hashKey, delta);
    }

    /**
     * set whole map into hash.
     *
     * @param key key
     * @param map map
     */
    public <V> void hashSetMap(String key, Map<String, V> map) {
        Assert.hasText(key, "key must not be blank");
        Assert.notNull(map, "value must not be blank");
        Class<?> mapValueClass = map.values().toArray()[0].getClass();
        if (mapValueClass == String.class) {
            stringRedisTemplate.opsForHash().putAll(key, map);
        } else {
            objValueRedisTemplate.opsForHash().putAll(key, map);
        }
    }

    /**
     * get whole map from hash.
     *
     * @param key key
     * @return map
     */
    public <V> Map<String, V> hashGetMap(String key) {
        Assert.hasText(key, "key must not be blank");
        HashOperations<String, String, V> operations = objValueRedisTemplate.opsForHash();
        return operations.entries(key);
    }

    public <V> List<V> hashGetMap(String key, Collection<String> hashKeys) {
        Assert.hasText(key, "key must not be blank");
        HashOperations<String, String, V> operations = objValueRedisTemplate.opsForHash();
        return operations.multiGet(key, hashKeys);
    }

    /**
     * get whole string map from hash.
     *
     * @param key String
     * @return Map<String, String>
     */
    public Map<String, String> hashGetStringMap(String key) {
        Assert.hasText(key, "key must not be blank");
        HashOperations<String, String, String> operations = stringRedisTemplate.opsForHash();
        return operations.entries(key);
    }

    public List<String> hashGetStringMap(String key, Collection<String> hashKeys) {
        Assert.hasText(key, "key must not be blank");
        HashOperations<String, String, String> operations = stringRedisTemplate.opsForHash();
        return operations.multiGet(key, hashKeys);
    }

    public Long hashRemoveKeys(String key, Object... hashKeys) {
        Assert.hasText(key, "key must not be blank");
        HashOperations<String, String, String> operations = stringRedisTemplate.opsForHash();
        return operations.delete(key, hashKeys);
    }

    /**
     * Get hash value according to class type and return the value. It uses clazz argument to
     * determine deserializer method as a consideration of compatible with media.
     * It assumes users know the real type of stored one.<br/>
     * Not suitable for String, primitive and wrapper class type, eg. Integer, Char...
     * If type is not clear, simply given 'Object' will be OK
     *
     * @param key     String
     * @param hashKey String
     * @param clazz Class
     * @return boolean that dedicates success or not
     * @see RedisService#hashGetString
     */
    public <T> T hashGetObject(String key, String hashKey, Class<T> clazz) {
        Assert.hasText(key, "key must not be blank");
        Assert.hasText(hashKey, "hashKey must not be blank");
        if (clazz == String.class) {
            throw new IllegalArgumentException("not supported for primitive, wrapper and String");
        }
        Object obj = objValueRedisTemplate.opsForHash().get(key, hashKey);
        return obj == null ? null : (T) obj;
    }

    /**
     * get primitive, boxing wrapper values or String and return String
     *
     * @param key String
     * @param hashKey String
     * @return String
     */
    public String hashGetString(String key, String hashKey) {
        Assert.hasText(key, "key must not be blank");
        return stringRedisTemplate.<String, String>opsForHash().get(key, hashKey);
    }

    /**
     * @param key     String
     * @param hashKey String
     *
     */
    public void deleteFromHash(String key, String hashKey) {
        Assert.hasText(key, "key must not be blank");
        Assert.hasText(hashKey, "hashKey must not be blank");
        objValueRedisTemplate.opsForHash().delete(key, hashKey);
    }

    /**
     * @param key   String
     * @param start int
     * @param end   int
     * @return normally {@code end–start+1} elements
     */
    public List<Object> listRangeObject(String key, int start, int end) {
        Assert.hasText(key, "key must not be blank");
        return objValueRedisTemplate.opsForList().range(key, start, end);
    }

    /**
     * @param key   String
     * @param start int
     * @param end   int
     * @return normally {@code end–start+1} elements
     */
    public List<String> listRangeString(String key, int start, int end) {
        Assert.hasText(key, "key must not be blank");
        return stringRedisTemplate.opsForList().range(key, start, end);
    }


    /**
     * Add one element to list from left position
     *
     * @param key   String
     * @param value Object
     *
     */
    public void leftPushToList(String key, Object value) {
        Assert.hasText(key, "key must not be blank");
        Assert.notNull(value, "value must not be null");
        if (ClassUtils.isPrimitiveOrWrapper(value.getClass()) || value.getClass() == String.class) {
            stringRedisTemplate.opsForList().leftPush(key, String.valueOf(value));
        } else {
            objValueRedisTemplate.opsForList().leftPush(key, value);
        }
    }

    /**
     * Add a bunch of values to list from left position. This method return a boolean
     * result that the caller must determine
     *
     * @param key    String
     * @param values List&lt;Object&gt;
     *
     */
    public <E> void leftPushAllToList(String key, List<E> values) {
        Assert.hasText(key, "key must not be blank");
        Assert.notEmpty(values, "values must not be empty");
        if (ClassUtils.isPrimitiveOrWrapper(values.get(0).getClass()) || values.get(0).getClass() == String.class) {
            List<String> stringValues = values.stream()
                    .map(String::valueOf)
                    .collect(Collectors.toList());
            stringRedisTemplate.opsForList().leftPushAll(key, stringValues);
        } else {
            objValueRedisTemplate.opsForList().leftPushAll(key, values);
        }
    }

    /**
     * Pop object from list according to class type and return the value. It uses clazz argument
     * to determine deserializer method as a consideration of compatible with media.
     * It assumes users know the real type of stored one.<br/>
     * Not suitable for primitive wrapper class type, eg. Integer, Char...
     * If type is not clear, simply given 'Object' will be OK
     *
     * @param key   String
     * @param clazz class of the value
     * @return T instance of clazz
     * @see RedisService#rightPopStringFromList
     */
    public <T> T rightPopObjectFromList(String key, Class<T> clazz) {
        Assert.hasText(key, "key must not be blank");
        Assert.notNull(clazz, "clazz must not be not null");
        if (ClassUtils.isPrimitiveOrWrapper(clazz) || clazz == String.class) {
            throw new IllegalArgumentException("not supported for primitive, wrapper and String");
        }
        Object obj = objValueRedisTemplate.opsForList().rightPop(key);
        return obj == null ? null : (T) obj;
    }

    public String rightPopStringFromList(String key) {
        Assert.hasText(key, "key must not be blank");
        return stringRedisTemplate.opsForList().rightPop(key);
    }

    public String leftPopStringFromList(String key) {
        Assert.hasText(key, "key must not be blank");
        return stringRedisTemplate.opsForList().leftPop(key);
    }

    public String listIndex(String key, int index) {
        Assert.hasText(key, "key must not be blank");
        return stringRedisTemplate.opsForList().index(key, index);
    }

    public Long listSize(String key) {
        Assert.hasText(key, "key must not be blank");
        return objValueRedisTemplate.opsForList().size(key);
    }

    public <T> T execCommand(String command, Class<T> resultClazz,
                             List<String> keys, Object... args) {
        Assert.hasText(command, "command must not be blank");
        Assert.notNull(resultClazz, "resultClazz must not be null");
        Assert.notNull(keys, "keys must not be null");
        return stringRedisTemplate.execute(new DefaultRedisScript<>(command,
                resultClazz), keys, args);
    }

    /**
     * get expire time for key
     */
    public Long getExpire(String key, TimeUnit timeUnit) {
        Assert.hasText(key, "key must not be blank");
        return stringRedisTemplate.getExpire(key, timeUnit);
    }
}
