package pers.zjw.xxxx.redis;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * redis settings properties
 *
 * @author zhangjw
 * @date 2022/12/20 23:13
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "spring.redis")
public class RedisSettingProperties {
    private String password;
}
