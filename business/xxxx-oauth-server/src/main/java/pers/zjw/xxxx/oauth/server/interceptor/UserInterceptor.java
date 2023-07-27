package pers.zjw.xxxx.oauth.server.interceptor;

import pers.zjw.xxxx.foundation.json.JsonParser;
import pers.zjw.xxxx.oauth.server.service.AuthorizationService;
import pers.zjw.xxxx.web.access.PathAccessibleMatcher;
import pers.zjw.xxxx.web.constant.AccessScope;
import pers.zjw.xxxx.web.constant.ErrorCode;
import pers.zjw.xxxx.web.context.ClientContextHolder;
import pers.zjw.xxxx.web.context.HeaderContextHolder;
import pers.zjw.xxxx.web.context.TransferableHeaders;
import pers.zjw.xxxx.web.pojo.ClientClaims;
import pers.zjw.xxxx.web.pojo.WebResponse;
import pers.zjw.xxxx.web.util.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * 用户信息拦截器
 *
 * @author zhangjw
 * @date 2023/12/26 0024 11:35
 */
@Slf4j
public class UserInterceptor implements HandlerInterceptor {
    private AuthorizationService authorizationService;
    private PathAccessibleMatcher matcher;

    public UserInterceptor(AuthorizationService authorizationService, PathAccessibleMatcher matcher) {
        this.authorizationService = authorizationService;
        this.matcher = matcher;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        TransferableHeaders headers = HeaderContextHolder.getInstance().getContext();
        String reqId = headers.requestId();
        log.debug("oauth server's user interceptor catch request {}, reqId:{}", request.getRequestURI(), reqId);

        if (AccessScope.PUBLIC.equals(matcher.match(headers.uri()))) {
            return true;
        }
        String authentication = headers.authorization();
        if (StringUtils.isNotEmpty(authentication)) {
            ClientClaims claims = JwtUtils.getClaimsFromAuthentication(authentication);
            if (AccessScope.AUTHENTICATED.equals(matcher.match(headers.uri()))) {
                log.debug("oauth server's user interceptor catch authenticated user but ignore authorization: {} {}, uri:{}",
                        claims.getClientId(), claims.getUsername(), request.getRequestURI());
                return true;
            }
            //调用签权服务验证用户是否有权限
            if (authorizationService.hasPermission(request.getServletPath(), request.getMethod())) {
                ClientContextHolder.getInstance().setContext(claims);
                log.debug("oauth server's user interceptor authorized uri:{}, clientId:{}, username:{}",
                        request.getRequestURI(), claims.getClientId(), claims.getUsername());
                return true;
            } else {
                log.warn("oauth server's user interceptor unauthorized uri:{}, clientId:{}, username:{}",
                        request.getRequestURI(), claims.getClientId(), claims.getUsername());
            }
        }
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json; charset=UTF-8");
        JsonParser.customize().writeValue(response.getWriter(),
                WebResponse.create(StringUtils.isNotBlank(reqId) ? reqId
                        : UUID.randomUUID().toString(), ErrorCode.UNAUTHENTICATED));
        return false;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex)
            throws Exception {
        ClientContextHolder.getInstance().clear();
    }
}
