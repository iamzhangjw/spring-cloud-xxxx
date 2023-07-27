package pers.zjw.xxxx.oauth.client.service;

import cn.hutool.core.util.StrUtil;
import pers.zjw.xxxx.oauth.client.provider.AuthProvider;
import pers.zjw.xxxx.web.constant.ErrorCode;
import pers.zjw.xxxx.web.pojo.AuthenticatedUser;
import pers.zjw.xxxx.web.pojo.WebResponse;
import pers.zjw.xxxx.web.context.HeaderContextHolder;
import pers.zjw.xxxx.web.util.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * auth service
 *
 * @author zhangjw
 * @date 2022/12/24 0024 17:28
 */
@Slf4j
@Service
class AuthService {
    @Autowired
    private AuthProvider provider;

    /**
     * jwt token 密钥，主要用于token解析，签名验证
     */
    @Value("${xxxx.oauth2.jwt.signingKey}")
    private String signingKey;

    @Value("${spring.application.name}")
    private String appName;


    WebResponse<AuthenticatedUser> authenticatedUser() {
        return provider.authenticatedUser();
    }

    WebResponse<Boolean> hasPermission(String authentication, String uri, String method) {
        return hasPermission(authentication, appName, uri, method);
    }

    WebResponse<Boolean> hasPermission(String authentication, String service, String uri, String method) {
        log.debug("check permission with {} for {} {} {}", service, authentication, uri, method);
        // 如果请求未携带token信息, 直接拒绝
        if (StrUtil.isBlank(authentication) || !authentication.startsWith(JwtUtils.BEARER)) {
            return WebResponse.create(HeaderContextHolder.getInstance().getReqId(),
                    ErrorCode.TOKEN_INVALID_OR_EXPIRED, "user token is null", Boolean.FALSE);
        }
        //token是否有效，在网关进行校验，无效/过期等
        if (!JwtUtils.verifyToken(authentication, signingKey)) {
            return WebResponse.create(HeaderContextHolder.getInstance().getReqId(),
                    ErrorCode.TOKEN_INVALID_OR_EXPIRED, "user token is invalid or expired", Boolean.FALSE);
        }
        //从认证服务获取是否有权限,远程调用
        return authenticate(service, uri, method);
    }

    private WebResponse<Boolean> authenticate(String service, String uri, String method) {
        return provider.hasPermission(service, uri, method);
    }
}
