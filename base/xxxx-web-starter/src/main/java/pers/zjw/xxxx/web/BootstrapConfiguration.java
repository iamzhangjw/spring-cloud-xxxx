package pers.zjw.xxxx.web;

import com.alibaba.csp.sentinel.annotation.aspectj.SentinelResourceAspect;
import pers.zjw.xxxx.foundation.json.JsonParser;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Collections;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * sprint boot web 启动配置项
 */
@EnableScheduling
@EnableAsync
@EnableTransactionManagement
@Configuration
public class BootstrapConfiguration {
    @Value("${spring.application.name:default}")
    private String appName;

    @Lazy
    @Bean("taskExecutor")
    public ThreadPoolTaskExecutor executorPool() {
        int size = Runtime.getRuntime().availableProcessors() * 2;
        ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
        pool.setCorePoolSize(size);
        pool.setMaxPoolSize(size);
        pool.setKeepAliveSeconds(60);
        pool.setQueueCapacity(1000);
        pool.setAllowCoreThreadTimeOut(true);
        pool.setThreadNamePrefix(appName + "-task-");
        pool.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return pool;
    }

    @Bean
    @ConditionalOnClass({MappingJackson2HttpMessageConverter.class})
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter(){
        MappingJackson2HttpMessageConverter jacksonConverter = new
                MappingJackson2HttpMessageConverter();
        jacksonConverter.setObjectMapper(JsonParser.customize());
        jacksonConverter.setSupportedMediaTypes
                (Collections.singletonList(MediaType.ALL));
        return jacksonConverter;
    }

    @Bean
    @ConditionalOnClass({SentinelResourceAspect.class})
    public SentinelResourceAspect sentinelResourceAspect() {
        return new SentinelResourceAspect();
    }

    @Bean
    @ConditionalOnMissingBean
    public HttpMessageConverters messageConverters(ObjectProvider<HttpMessageConverter<?>> converters) {
        return new HttpMessageConverters(converters.orderedStream().collect(Collectors.toList()));
    }
}
