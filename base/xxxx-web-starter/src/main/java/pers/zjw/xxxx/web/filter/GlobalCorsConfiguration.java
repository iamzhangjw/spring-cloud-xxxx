package pers.zjw.xxxx.web.filter;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * GlobalCorsConfiguration
 *
 * @author zhangjw
 * @date 2023/01/07 15:12
 */
@ConditionalOnClass({CorsFilter.class})
@Configuration
public class GlobalCorsConfiguration {
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        // 开放ip、端口、域名的访问权限，星号表示开放所有域
        config.addAllowedOrigin("*");
        //是否允许发送Cookie信息
        config.setAllowCredentials(true);
        config.addAllowedMethod(HttpMethod.DELETE);
        config.addAllowedMethod(HttpMethod.POST);
        config.addAllowedMethod(HttpMethod.GET);
        config.addAllowedMethod(HttpMethod.PUT);
        config.addAllowedMethod(HttpMethod.HEAD);
        config.addAllowedMethod(HttpMethod.OPTIONS);
        //允许HTTP请求中的携带哪些Header信息
        config.addAllowedHeader("*");

        //添加映射路径，“/**”表示对所有的路径实行全局跨域访问权限的设置
        UrlBasedCorsConfigurationSource configSource = new UrlBasedCorsConfigurationSource();
        configSource.registerCorsConfiguration("/**", config);

        return new CorsFilter(configSource);
    }
}
