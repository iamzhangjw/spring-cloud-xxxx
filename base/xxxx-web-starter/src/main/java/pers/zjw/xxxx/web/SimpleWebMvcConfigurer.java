package pers.zjw.xxxx.web;

import pers.zjw.xxxx.web.interceptor.AddHeaderContextInterceptor;
import pers.zjw.xxxx.web.interceptor.LogTrackInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * web mvc configurer
 *
 * @author zhangjw
 * @date 2022/12/24 0024 17:06
 */
public class SimpleWebMvcConfigurer implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AddHeaderContextInterceptor()).addPathPatterns("/**");
        registry.addInterceptor(new LogTrackInterceptor()).addPathPatterns("/**");
    }
}
