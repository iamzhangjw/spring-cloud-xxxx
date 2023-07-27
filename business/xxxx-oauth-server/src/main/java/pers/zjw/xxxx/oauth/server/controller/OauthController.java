package pers.zjw.xxxx.oauth.server.controller;

import cn.hutool.core.codec.Base64;
import pers.zjw.xxxx.oauth.server.constant.GrantType;
import pers.zjw.xxxx.oauth.server.constant.OauthErrorCode;
import pers.zjw.xxxx.oauth.server.constant.Scope;
import pers.zjw.xxxx.oauth.server.exception.OauthException;
import pers.zjw.xxxx.oauth.server.pojo.entity.OauthCode;
import pers.zjw.xxxx.oauth.server.service.OauthService;
import pers.zjw.xxxx.web.controller.BaseController;
import pers.zjw.xxxx.web.pojo.AccessToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;

/**
 * oauth controller
 *
 * want known more about oauth2.0, see <a href="https://www.ruanyifeng.com/blog/2014/05/oauth_2_0.html">理解OAuth 2.0</a>
 *
 * @author zhangjw
 * @date 2022/12/26 0026 15:42
 */
@Slf4j
@RequestMapping("/oauth")
@RestController
public class OauthController extends BaseController {
    @Autowired
    private OauthService oauthService;

    @PostMapping(value = "/token", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public AccessToken token(@RequestHeader(HttpHeaders.AUTHORIZATION) String authentication,
                             @RequestParam Map<String, String> params) {
        // 只支持 Basic Auth
        if (StringUtils.isEmpty(authentication) || !authentication.startsWith("Basic ") || authentication.length() < 7) {
            throw new OauthException(OauthErrorCode.INVALID_REQUEST, "invalid authentication");
        }
        String[] clientDetails = Base64.decodeStr(authentication.substring(6)).split(":");
        if (clientDetails.length != 2) throw new OauthException(OauthErrorCode.INVALID_REQUEST, "invalid authentication");

        String scope = params.get("scope");
        String grantType = params.get("grant_type");

        Optional<GrantType> validGrantType = GrantType.parse(grantType);
        if (!validGrantType.isPresent()) throw new OauthException(OauthErrorCode.INVALID_GRANT, "grant invalid");
        if (!validGrantType.get().support()) throw new OauthException(OauthErrorCode.UNSUPPORTED_GRANT_TYPE);

        Set<Scope> validScope = new HashSet<>();
        if (StringUtils.hasText(scope)) {
            String[] scopeArr = scope.split(",");
            for (String scopeItem : scopeArr) {
                Optional<Scope> scopeOptional = Scope.parse(scopeItem);
                if (scopeOptional.isPresent()) {
                    validScope.add(scopeOptional.get());
                } else {
                    throw new OauthException(OauthErrorCode.INVALID_SCOPE, "scope invalid");
                }
            }
        }

        try {
            return oauthService.token(clientDetails[0], clientDetails[1],
                    validScope, validGrantType.get(), params);
        } catch (Exception e) {
            log.error("get token failed", e);
            throw new OauthException(OauthErrorCode.SERVER_ERROR);
        }
    }

    @GetMapping("/authorize")
    public ModelAndView authorize(@CookieValue(value = "refresh_token", required = false) String refreshToken,
                                  @RequestParam Map<String,String> params) {
        if (null == refreshToken || !oauthService.verifyToken(refreshToken)) {
            return new ModelAndView("redirect:/login", params);
        }
        return new ModelAndView("authorize", params);
    }

    @PostMapping(value = "/authorize", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public ModelAndView authorize(@RequestParam Map<String, String> params,
                                  @CookieValue(value = "refresh_token") String refreshToken) {
        String responseType = params.get("response_type");
        String clientId = params.get("client_id");
        String redirectUri = params.get("redirect_uri");
        String scope = params.get("scope");
        String state = params.get("state");
        if (!"code".equals(responseType)) throw new OauthException(OauthErrorCode.UNSUPPORTED_RESPONSE_TYPE);
        Set<Scope> validScope = new HashSet<>();
        if (StringUtils.hasText(scope)) {
            String[] scopeArr = scope.split(",");
            for (String scopeItem : scopeArr) {
                Optional<Scope> scopeOptional = Scope.parse(scopeItem);
                if (scopeOptional.isPresent()) {
                    validScope.add(scopeOptional.get());
                } else {
                    throw new OauthException(OauthErrorCode.INVALID_SCOPE, "scope invalid");
                }
            }
        }
        Map<String, Object> map = new HashMap<>();
        if (StringUtils.hasText(state)) {
            map.put("state", state);
        }
        if (StringUtils.hasText(params.get("approval"))) {
            boolean approval = Boolean.parseBoolean(params.get("approval"));
            if (approval) {
                OauthCode code = oauthService.oauthCode(clientId, redirectUri, validScope, refreshToken);
                map.put("code", code.getCode());
            }
        } else {
            map.put("error", "access_denied");
            map.put("error_description", "User denied access");
        }
        return new ModelAndView("redirect:"+redirectUri, map);
    }
}
