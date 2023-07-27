package pers.zjw.xxxx.web.filter;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;

/**
 * servlet component register, such as filter, interceptor, listener
 *
 * jdk 提供的 WebFilter 不支持加载顺序，因此需要借助 FilterRegistrationBean 的方式注入
 *
 *
 * @date 2022/06/09 0009 15:01
 * @author zhangjw
 */
@Configuration
@ConditionalOnClass({FilterRegistrationBean.class, Filter.class})
public class CommonFilterRegister {

    @Bean
    public FilterRegistrationBean<ExceptionFilter> exceptionFilter() {
        FilterRegistrationBean<ExceptionFilter> filterRegBean = new FilterRegistrationBean<>();
        filterRegBean.setFilter(new ExceptionFilter());
        filterRegBean.setName("exceptionFilter");
        filterRegBean.addUrlPatterns("/*");
        filterRegBean.setOrder(0);
        return filterRegBean;
    }

    @Bean
    public FilterRegistrationBean<RequestStreamFilter> requestStreamFilter() {
        FilterRegistrationBean<RequestStreamFilter> filterRegBean = new FilterRegistrationBean<>();
        filterRegBean.setFilter(new RequestStreamFilter());
        filterRegBean.setName("requestStreamFilter");
        filterRegBean.addUrlPatterns("/*");
        filterRegBean.setOrder(-1);
        return filterRegBean;
    }
}
