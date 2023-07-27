package pers.zjw.xxxx.redis.cache;

/**
 * CacheCreateException
 *
 * @author zhangjw
 * @date 2023/01/31 17:50
 */
public class CacheCreateException extends Exception {
    public CacheCreateException() {
        super();
    }

    public CacheCreateException(String message) {
        super(message);
    }

    public CacheCreateException(String name, String key) {
        super(name + ((key == null)
                ? ""
                : " (" + key + ")"));
    }
}
