package pers.zjw.xxxx.oauth.client.interceptor;

import pers.zjw.xxxx.foundation.json.JsonParser;
import pers.zjw.xxxx.oauth.client.service.PermissionService;
import pers.zjw.xxxx.web.access.PathAccessibleMatcher;
import pers.zjw.xxxx.web.access.TrustedAccessCertifier;
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
 * @date 2022/12/26 0024 11:35
 */
@Slf4j
public class UserInterceptor implements HandlerInterceptor {
    private TrustedAccessCertifier certifier;
    private PermissionService permissionService;
    private PathAccessibleMatcher matcher;

    public UserInterceptor(TrustedAccessCertifier certifier, PermissionService permissionService, PathAccessibleMatcher matcher) {
        this.certifier = certifier;
        this.permissionService = permissionService;
        this.matcher = matcher;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        TransferableHeaders headers = HeaderContextHolder.getInstance().getContext();
        String reqId = headers.requestId();
        log.debug("user interceptor catch request {}, reqId:{}", request.getRequestURI(), reqId);
        //从网关获取并校验,通过校验就可信任 x-client-token-user 中的信息
        if (certifier.verify(headers.clientToken())) {
            String userInfoString = StringUtils.defaultIfBlank(headers.clientPayload(), JsonParser.defaultString());
            ClientClaims claims = JsonParser.toObject(userInfoString, ClientClaims.class);
            log.debug("user interceptor catch authenticated user from gateway: {} {}, uri:{}",
                    claims.getClientId(), claims.getUsername(), request.getRequestURI());
            ClientContextHolder.getInstance().setContext(claims);
            return true;
        } else {
            if (AccessScope.PUBLIC.equals(matcher.match(headers.uri()))) {
                return true;
            }
            String authentication = headers.authorization();
            if (StringUtils.isNotEmpty(authentication)) {
                ClientClaims claims = JwtUtils.getClaimsFromAuthentication(authentication);
                if (AccessScope.AUTHENTICATED.equals(matcher.match(headers.uri()))) {
                    log.debug("oauth client's user interceptor catch authenticated user but ignore authorization: {} {}, uri:{}",
                            claims.getClientId(), claims.getUsername(), request.getRequestURI());
                    return true;
                }
                //调用签权服务验证用户是否有权限
                WebResponse<Boolean> hasPermission = permissionService.hasPermission(authentication,
                        request.getServletPath(), request.getMethod());
                if (hasPermission.isSuccess() && hasPermission.getData()) {
                    ClientContextHolder.getInstance().setContext(claims);
                    log.debug("oauth client's user interceptor authorized uri:{}, clientId:{}, username:{}",
                            request.getRequestURI(), claims.getClientId(), claims.getUsername());
                    return true;
                } else {
                    log.warn("oauth client's user interceptor unauthorized uri:{}, clientId:{}, username:{}",
                            request.getRequestURI(), claims.getClientId(), claims.getUsername());
                }
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
