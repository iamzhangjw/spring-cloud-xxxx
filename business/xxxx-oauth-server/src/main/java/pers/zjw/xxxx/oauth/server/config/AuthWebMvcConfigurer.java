package pers.zjw.xxxx.oauth.server.config;

import pers.zjw.xxxx.oauth.server.interceptor.UserInterceptor;
import pers.zjw.xxxx.oauth.server.service.AuthorizationService;
import pers.zjw.xxxx.web.SimpleWebMvcConfigurer;
import pers.zjw.xxxx.web.access.PathAccessibleMatcher;
import pers.zjw.xxxx.web.constant.AccessScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

import java.util.List;

/**
 * auth web mvc configurer
 *
 * @author zhangjw
 * @date 2022/12/24 0024 17:06
 */
@Configuration
public class AuthWebMvcConfigurer extends SimpleWebMvcConfigurer {
    @Value("${spring.application.name}")
    private String appName;
    @Value("${server.error.path:${error.path:/error}}")
    private String errorPath;
    @Autowired
    private AuthorizationService authorizationService;
    @Autowired
    private PathAccessibleMatcher matcher;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        super.addInterceptors(registry);

        InterceptorRegistration registration = registry
                .addInterceptor(new UserInterceptor(authorizationService, matcher))
                .addPathPatterns("/**");
        List<String> paths = matcher.getPath(AccessScope.PUBLIC, appName);
        if (!CollectionUtils.isEmpty(paths)) {
            registration.excludePathPatterns(paths);
        }
        registration.excludePathPatterns(errorPath + "/**");
    }
}
