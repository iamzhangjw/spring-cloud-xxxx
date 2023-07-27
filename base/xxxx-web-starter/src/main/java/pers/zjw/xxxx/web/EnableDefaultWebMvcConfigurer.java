package pers.zjw.xxxx.web;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * EnableDefaultWebMvcConfigurer
 *
 * @author zhangjw
 * @date 2023/02/14 23:17
 */
@ConditionalOnClass({WebMvcConfigurer.class})
@ConditionalOnMissingBean({SimpleWebMvcConfigurer.class})
@Configuration
public class EnableDefaultWebMvcConfigurer {
    @Bean
    public SimpleWebMvcConfigurer simpleWebMvcConfigurer() {
        return new SimpleWebMvcConfigurer();
    }
}
