package pers.zjw.xxxx.redis.cache;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;
import java.util.Map;

/**
 * CacheProperties
 *
 * @author zhangjw
 * @date 2023/01/19 0019 14:51
 */
@Configuration
@ConfigurationProperties(
        prefix = "xxxx.cache"
)
@Data
public class CacheProperties {
    private Entry local;
    private Entry remote;

    @Data
    protected static class Entry {
        private Boolean enable = true;
        private Collection<Map<String, Object>> entries;
    }
}
