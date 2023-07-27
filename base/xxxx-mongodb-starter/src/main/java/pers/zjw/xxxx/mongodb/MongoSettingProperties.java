package pers.zjw.xxxx.mongodb;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * mongo 自定义配置
 *
 * @date 2022/03/30 0030 20:11
 * @author zhangjw
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "spring.data.mongodb")
public class MongoSettingProperties {
    private String username;
    private char[] password;

    private String uri;
    private Integer maxSize = 10;
    private Integer minSize = 2;
    private Integer maxIdleTime = 60000;
    private Integer maxWaitTime = 30000;
}
