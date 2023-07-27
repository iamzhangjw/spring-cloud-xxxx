package pers.zjw.xxxx.gateway.filter;

import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.support.ConfigurationService;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 限制请求访问频率过滤器
 *
 * @author zhangjw
 * @date 2022/12/24 0024 14:01
 */
public class DefaultRedisRateLimiter extends RedisRateLimiter {

    public DefaultRedisRateLimiter(
            ReactiveStringRedisTemplate redisTemplate, RedisScript<List<Long>> script,
            ConfigurationService configurationService) {
        super(redisTemplate, script, configurationService);
    }

    @Override
    public Mono<Response> isAllowed(String routeId, String id) {
        if (null == super.getConfig().get(routeId)) {
            getConfig().put(routeId, getDefaultConfig());
        }
        return super.isAllowed(routeId, id);
    }

    Config getDefaultConfig() {
        return super.getConfig().get("defaultFilters");
    }
}
