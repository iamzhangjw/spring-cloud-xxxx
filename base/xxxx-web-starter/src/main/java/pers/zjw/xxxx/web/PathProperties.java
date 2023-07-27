package pers.zjw.xxxx.web;

import pers.zjw.xxxx.web.pojo.PathStrategy;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.Collection;

/**
 * 路径放开访问策略配置
 *
 * @author zhangjw
 * @date 2023/02/09 22:32
 */
@Configuration
@ConfigurationProperties(prefix = "xxxx.path")
@PropertySource(value = "classpath:path.properties", ignoreResourceNotFound = true)
public class PathProperties {
    private Collection<PathStrategy> strategies;

    private String ignorePaths;

    public Collection<PathStrategy> getStrategies() {
        return strategies;
    }

    public void setStrategies(Collection<PathStrategy> strategies) {
        this.strategies = strategies;
    }

    public String getIgnorePaths() {
        return ignorePaths;
    }

    public void setIgnorePaths(String ignorePaths) {
        this.ignorePaths = ignorePaths;
    }
}
