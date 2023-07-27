package pers.zjw.xxxx.oauth.client.service;

import pers.zjw.xxxx.web.pojo.AuthenticatedUser;
import pers.zjw.xxxx.web.pojo.WebResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * permission service
 *
 * @author zhangjw
 * @date 2022/12/24 0024 17:28
 */
@Service
public class PermissionService {
    /**
     * 由 xxxx-oauth-client 模块提供签权的 feign 客户端
     */
    @Autowired
    private AuthService authService;

    public WebResponse<Boolean> hasPermission(String authentication, String service, String uri, String method) {
        return authService.hasPermission(authentication, service, uri, method);
    }

    public WebResponse<Boolean> hasPermission(String authentication, String uri, String method) {
        return authService.hasPermission(authentication, uri, method);
    }

    public AuthenticatedUser authenticatedUser() {
        WebResponse<AuthenticatedUser> response = authService.authenticatedUser();
        if (response.isSuccess() && null != response.getData()) return response.getData();
        return AuthenticatedUser.NO_ONE;
    }
}
