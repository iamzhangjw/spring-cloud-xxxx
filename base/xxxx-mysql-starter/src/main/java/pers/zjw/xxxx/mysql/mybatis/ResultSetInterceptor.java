package pers.zjw.xxxx.mysql.mybatis;

import cn.hutool.core.util.ArrayUtil;
import pers.zjw.xxxx.foundation.json.JsonParser;
import pers.zjw.xxxx.mysql.CryptoUtils;
import pers.zjw.xxxx.mysql.annotation.Crypto;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.plugin.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;

import java.sql.Statement;
import java.util.*;

/**
 * result set interceptor
 *
 * @author zhangjw
 * @date 2023/01/16 0016 16:57
 */
@Slf4j
@ConditionalOnProperty("xxxx.mysql.secretKey")
@Component
@Intercepts({
        @Signature(type = ResultSetHandler.class, method = "handleResultSets", args = {Statement.class})
})
public class ResultSetInterceptor implements Interceptor {
    @Value("${xxxx.mysql.secretKey}")
    private String secretKey;


    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object resultObject = invocation.proceed();
        if (null == resultObject || ClassUtils.isPrimitiveOrWrapper(resultObject.getClass())
                || ClassUtils.isPrimitiveArray(resultObject.getClass())) {
            return resultObject;
        }
        if (resultObject instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) resultObject;
            if (CollectionUtils.isEmpty(map)) return resultObject;
            Optional<?> o = map.values().stream().filter(Objects::nonNull).findFirst();
            if (!o.isPresent()) return resultObject;
            for (Object value : map.values()) {
                if (null != value.getClass().getAnnotation(Crypto.class)) {
                    CryptoUtils.decrypt(value, secretKey);
                }
            }
        } else if (resultObject instanceof Collection) {
            Collection<?> collection = (Collection<?>) resultObject;
            if (CollectionUtils.isEmpty(collection)) return resultObject;
            Optional<?> o = collection.stream().filter(Objects::nonNull).findFirst();
            if (!o.isPresent()) return resultObject;
            if (null != o.get().getClass().getAnnotation(Crypto.class)) {
                CryptoUtils.decrypt(collection, secretKey);
            }
        } else if (resultObject.getClass().isArray()) {
            if (ArrayUtil.isEmpty(resultObject)) return resultObject;
            CryptoUtils.decrypt((Object[]) resultObject, secretKey);
        } else if (resultObject instanceof String) {
            return resultObject;
        } else {
            if (null != resultObject.getClass().getAnnotation(Crypto.class)) {
                CryptoUtils.decrypt(resultObject, secretKey);
            }
        }
        log.debug("decrypt mybatis resultSet: {}", JsonParser.toString(resultObject));
        return resultObject;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }
}
