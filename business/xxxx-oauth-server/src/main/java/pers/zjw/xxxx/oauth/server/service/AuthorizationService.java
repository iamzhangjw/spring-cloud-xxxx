package pers.zjw.xxxx.oauth.server.service;

import pers.zjw.xxxx.oauth.server.pojo.entity.AuthUser;
import pers.zjw.xxxx.oauth.server.pojo.vo.ApiResource;
import pers.zjw.xxxx.oauth.server.util.ApiResourceValidator;
import pers.zjw.xxxx.web.context.HeaderContextHolder;
import pers.zjw.xxxx.web.context.TransferableHeaders;
import pers.zjw.xxxx.web.pojo.AuthenticatedUser;
import pers.zjw.xxxx.web.pojo.ClientClaims;
import pers.zjw.xxxx.web.util.JwtUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collection;

/**
 * AuthorizationService
 *
 * @author zhangjw
 * @date 2023/01/05 10:26
 */
@Service
public class AuthorizationService {
    @Autowired
    private AuthApiResourceService apiResourceService;
    @Autowired
    private AuthRoleApiResourceRelationService roleApiResourceRelationService;
    @Autowired
    private AuthUserService userService;
    @Value("${spring.application.name}")
    private String appName;

    public boolean hasPermission(String uri, String method) {
        return hasPermission(appName, uri, method);
    }

    public boolean hasPermission(String service, String uri, String method) {
        TransferableHeaders headers = HeaderContextHolder.getInstance().getContext();
        if (null == headers) return false;
        String authentication = headers.authorization();
        if (StringUtils.isBlank(authentication)) return false;

        ClientClaims claims = JwtUtils.getClaimsFromAuthentication(authentication);
        String username = claims.getUsername();
        Collection<ApiResource> includes;
        if (StringUtils.isNotBlank(username)) {
            includes = roleApiResourceRelationService.listByUsername(username);
        } else {
            String roleCode = claims.getRole();
            if (StringUtils.isBlank(roleCode)) return false;
            includes = roleApiResourceRelationService.listByRoleCode(roleCode);
        }
        ApiResourceValidator validator;
        if (CollectionUtils.isEmpty(includes)) {
            validator = ApiResourceValidator.create();
        } else {
            validator = ApiResourceValidator.create(includes);
        }
        Collection<ApiResource> excludes = apiResourceService.allExcludes();
        if (!CollectionUtils.isEmpty(excludes)) {
            validator.excludeResources(excludes);
        }
        return validator.validate(StringUtils.isBlank(service) ? appName : service, uri, method);
    }

    public AuthenticatedUser authenticatedUser() {
        TransferableHeaders headers = HeaderContextHolder.getInstance().getContext();
        if (null == headers) return AuthenticatedUser.NO_ONE;
        String authentication = headers.authorization();
        if (StringUtils.isBlank(authentication)) return AuthenticatedUser.NO_ONE;

        ClientClaims claims = JwtUtils.getClaimsFromAuthentication(authentication);
        String username = claims.getUsername();
        AuthUser user = userService.loadByUsername(username);
        if (null == user) return AuthenticatedUser.NO_ONE;
        return user.to();
    }
}