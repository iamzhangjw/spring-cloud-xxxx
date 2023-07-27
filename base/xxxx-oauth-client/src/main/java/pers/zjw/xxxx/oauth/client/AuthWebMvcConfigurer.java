package pers.zjw.xxxx.oauth.client;

import pers.zjw.xxxx.oauth.client.context.CurrentUserArgumentResolver;
import pers.zjw.xxxx.oauth.client.interceptor.UserInterceptor;
import pers.zjw.xxxx.oauth.client.service.PermissionService;
import pers.zjw.xxxx.web.SimpleWebMvcConfigurer;
import pers.zjw.xxxx.web.access.PathAccessibleMatcher;
import pers.zjw.xxxx.web.access.TrustedAccessCertifier;
import pers.zjw.xxxx.web.constant.AccessScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * auth web mvc configurer
 *
 * @author zhangjw
 * @date 2022/12/24 0024 17:06
 */
@ConditionalOnClass({WebMvcConfigurer.class})
@Configuration
public class AuthWebMvcConfigurer extends SimpleWebMvcConfigurer {
    @Value("${spring.application.name}")
    private String appName;
    @Value("${server.error.path:${error.path:/error}}")
    private String errorPath;
    @Autowired
    private TrustedAccessCertifier certifier;
    @Autowired
    private PathAccessibleMatcher matcher;
    @Autowired
    private PermissionService permissionService;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        super.addInterceptors(registry);
        InterceptorRegistration registration = registry
                .addInterceptor(new UserInterceptor(certifier, permissionService, matcher))
                .addPathPatterns("/**");
        List<String> paths = matcher.getPath(AccessScope.PUBLIC, appName);
        if (!CollectionUtils.isEmpty(paths)) {
            registration.excludePathPatterns(paths);
        }
        registration.excludePathPatterns(errorPath + "/**");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new CurrentUserArgumentResolver(permissionService));
    }

    public static void main(String[] args) {
        AntPathMatcher matcher = new AntPathMatcher();
        System.out.println(matcher.match("/oauth/**", "/oauth"));
        System.out.println(matcher.match("/oauth/**", "/oauth/abc"));
        System.out.println(matcher.match("/oauth/**", "/oauth/abc/def"));
        System.out.println(matcher.match("/oauth/**", "/oauth2"));
    }
}
