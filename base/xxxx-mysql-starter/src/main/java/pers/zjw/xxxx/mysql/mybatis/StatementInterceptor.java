package pers.zjw.xxxx.mysql.mybatis;

import pers.zjw.xxxx.mysql.EncryptionArgsHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.*;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * StatementInterceptor
 *
 * @author zhangjw
 * @date 2023/01/24 19:29
 */
@Slf4j
@Component
@Intercepts({
        @Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})
})
public class StatementInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        BoundSql boundSql = statementHandler.getBoundSql();

        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        Map<String, String> encryptionArgs = EncryptionArgsHolder.getInstance().getContext();
        if (CollectionUtils.isEmpty(encryptionArgs) || CollectionUtils.isEmpty(parameterMappings)) {
            return invocation.proceed();
        }
        Object parameterObject = boundSql.getParameterObject();
        if (parameterObject instanceof Map) {
            Map<String, Object> map = (Map<String, Object>)parameterObject;
            encryptionArgs.forEach((key, value) -> {
                if (map.containsKey(key)) {
                    try {
                        map.put(key, value);
                    } catch (Exception e) {
                        log.error("replace plain arg in boundSql to cipher failed.", e);
                    }
                }
            });
        }
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }
}
