package pers.zjw.xxxx.oauth.client.context;

import pers.zjw.xxxx.oauth.client.service.PermissionService;
import pers.zjw.xxxx.web.context.ClientContextHolder;
import pers.zjw.xxxx.web.pojo.AuthenticatedUser;
import pers.zjw.xxxx.web.pojo.ClientClaims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * CurrentUser 注解参数注入
 *
 * @author zhangjw
 * @date 2022/12/30 0030 10:45
 *
 * see <a href="https://www.baeldung.com/spring-mvc-custom-data-binder">A Custom Data Binder in Spring MVC</a>
 */
@Slf4j
public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {
    private PermissionService permissionService;

    public CurrentUserArgumentResolver(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return methodParameter.hasParameterAnnotation(CurrentUser.class)
                && methodParameter.getParameterType().equals(AuthenticatedUser.class);
    }

    @Override
    public Object resolveArgument(
            MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer,
            NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        CurrentUser annotation = methodParameter.getParameterAnnotation(CurrentUser.class);
        if (null == annotation) return null;
        ServletWebRequest request = (ServletWebRequest) nativeWebRequest;
        log.debug("resolve @CurrentUser annotation on {}", request.getRequest().getRequestURI());
        ClientClaims claims = ClientContextHolder.getInstance().getContext();
        if (claims == null || StringUtils.isEmpty(claims.getUsername())) return AuthenticatedUser.NO_ONE;
        return permissionService.authenticatedUser();
    }
}
