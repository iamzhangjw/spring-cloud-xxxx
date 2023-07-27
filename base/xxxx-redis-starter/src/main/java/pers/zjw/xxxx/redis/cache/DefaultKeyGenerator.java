package pers.zjw.xxxx.redis.cache;

import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;

/**
 * DefaultKeyGenerator
 *
 * @author zhangjw
 * @date 2023/02/22 21:17
 */
public class DefaultKeyGenerator implements KeyGenerator {
    @Override
    public Object generate(Object target, Method method, Object... params) {
        return target.getClass().getSimpleName() + "."
                + method.getName() + ":"
                + StringUtils.arrayToDelimitedString(params, "_");
    }
}
