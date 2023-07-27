package pers.zjw.xxxx.oauth.server.controller;

import cn.hutool.core.codec.Base64;
import pers.zjw.xxxx.oauth.server.constant.GrantType;
import pers.zjw.xxxx.oauth.server.constant.Scope;
import pers.zjw.xxxx.oauth.server.service.AuthRoleWebResourceRelationService;
import pers.zjw.xxxx.oauth.server.service.AuthUserService;
import pers.zjw.xxxx.oauth.server.service.OauthService;
import pers.zjw.xxxx.web.pojo.AccessToken;
import pers.zjw.xxxx.oauth.server.pojo.entity.AuthUser;
import pers.zjw.xxxx.oauth.server.pojo.vo.LoggedUser;
import pers.zjw.xxxx.oauth.server.pojo.vo.WebResource;
import pers.zjw.xxxx.web.constant.ErrorCode;
import pers.zjw.xxxx.web.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * login controller
 *
 * @author zhangjw
 * @date 2023/01/06 23:17
 */
@Slf4j
@RequestMapping("/login")
@RestController
public class LoginController {
    @Autowired
    private OauthService oauthService;
    @Autowired
    private AuthUserService userService;
    @Autowired
    private AuthRoleWebResourceRelationService roleWebResourceRelationService;

    @Value("${xxxx.oauth2.client-id}")
    private String defaultClientId;
    @Value("${xxxx.oauth2.client-secret}")
    private String defaultClientSecret;
    @Value("${xxxx.oauth2.scope}")
    private String defaultScopeStr;

    @GetMapping
    public ModelAndView login(@RequestParam Map<String,String> params) {
        return new ModelAndView("login", params);
    }

    @PostMapping(consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public RedirectView login(@RequestParam Map<String, String> params,
                              RedirectAttributes attributes, HttpServletResponse response) {
        LoggedUser loggedUser = login(defaultClientId, defaultClientSecret, defaultScope, params);
        Cookie cookie1 = new Cookie("access_token", loggedUser.getToken().getAccessToken());
        cookie1.setMaxAge(loggedUser.getToken().getExpiresIn());
        response.addCookie(cookie1);
        Cookie cookie2 = new Cookie("refresh_token", loggedUser.getToken().getRefreshToken());
        cookie2.setMaxAge(2_592_000);
        response.addCookie(cookie2);
        Cookie cookie3 = new Cookie("user_name", loggedUser.getNickname());
        cookie3.setMaxAge(2_592_000);
        response.addCookie(cookie3);
        params.forEach((k, v) -> {
            if (!"username".equals(k) && !"password".equals(k)) {
                try {
                    attributes.addAttribute(URLDecoder.decode(k, StandardCharsets.UTF_8.name()),
                            URLDecoder.decode(v, StandardCharsets.UTF_8.name()));
                } catch (UnsupportedEncodingException e) {
                    log.warn("Illegal url attribute:{}={}", k, v, e);
                }
            }
        });
        return new RedirectView("oauth/authorize");
    }

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE})
    public LoggedUser login(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authentication,
                            @RequestBody Map<String, String> params) {
        String clientId = defaultClientId, clientSecret = defaultClientSecret;
        if (StringUtils.hasText(authentication) && authentication.startsWith("Basic ")) {
            String[] clientDetails = Base64.decodeStr(authentication.substring(6)).split(":");
            clientId = clientDetails[0];
            clientSecret = clientDetails[1];
        }
        String scope = params.get("scope");
        Collection<Scope> validScope = defaultScope;
        if (StringUtils.hasText(scope)) {
            validScope = new HashSet<>();
            String[] scopeArr = scope.split(",");
            for (String scopeItem : scopeArr) {
                Optional<Scope> scopeOptional = Scope.parse(scopeItem);
                if (scopeOptional.isPresent()) {
                    validScope.add(scopeOptional.get());
                } else {
                    throw new BizException(ErrorCode.AUTHORIZE_FAILED, "scope非法");
                }
            }
            if (CollectionUtils.isEmpty(validScope)) {
                throw new BizException(ErrorCode.AUTHORIZE_FAILED, "scope非法");
            }
        }
        return login(clientId, clientSecret, validScope, params);
    }

    private LoggedUser login(String clientId, String clientSecret, Collection<Scope> scope, Map<String, String> params) {
        try {
            AccessToken token = oauthService.token(clientId, clientSecret, scope, GrantType.PASSWORD, params);
            AuthUser user = userService.loadByUsername(params.get("username"));
            Collection<WebResource> permissions = roleWebResourceRelationService.cascadeByUser(user.getId());
            LoggedUser loggedUser = new LoggedUser();
            loggedUser.setId(user.getId());
            loggedUser.setName(user.getName());
            loggedUser.setNickname(user.getNickname());
            loggedUser.setToken(token);
            loggedUser.setPermissions(permissions);
            return loggedUser;
        } catch (RuntimeException e) {
            throw new BizException(ErrorCode.AUTHORIZE_FAILED, "登录失败");
        }
    }

    private Collection<Scope> defaultScope;

    @PostConstruct
    void scope() {
        defaultScope = new HashSet<>();
        String[] scopeArr = defaultScopeStr.split(",");
        for (String scopeItem : scopeArr) {
            Optional<Scope> scopeOptional = Scope.parse(scopeItem);
            scopeOptional.ifPresent(defaultScope::add);
        }
    }
}
