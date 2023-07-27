package pers.zjw.xxxx.mysql;

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
@ConfigurationProperties(prefix = "spring.datasource")
public class MysqlSettingProperties {
    private String username;
    private String password;
    private String uri;
}
