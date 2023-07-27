package pers.zjw.xxxx.oauth.server.service;

import cn.hutool.crypto.digest.DigestUtil;
import pers.zjw.xxxx.web.pojo.AccessToken;
import pers.zjw.xxxx.oauth.server.authentication.AuthenticatorManager;
import pers.zjw.xxxx.oauth.server.constant.GrantType;
import pers.zjw.xxxx.oauth.server.constant.OauthErrorCode;
import pers.zjw.xxxx.oauth.server.constant.Scope;
import pers.zjw.xxxx.oauth.server.exception.InvalidClientException;
import pers.zjw.xxxx.oauth.server.exception.OauthException;
import pers.zjw.xxxx.oauth.server.pojo.entity.OauthClientDetails;
import pers.zjw.xxxx.oauth.server.pojo.entity.OauthCode;
import pers.zjw.xxxx.oauth.server.pojo.entity.AuthUser;
import pers.zjw.xxxx.web.constant.ConstLiteral;
import pers.zjw.xxxx.web.exception.BizException;
import pers.zjw.xxxx.web.pojo.AuthenticatedUser;
import pers.zjw.xxxx.web.pojo.ClientClaims;
import pers.zjw.xxxx.web.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * oauth service
 *
 * @author zhangjw
 * @date 2022/12/26 0026 15:43
 */
@Service
public class OauthService {
    @Autowired
    private OauthClientDetailsService clientDetailsService;
    @Autowired
    private OauthAccessTokenService accessTokenService;
    @Autowired
    private OauthRefreshTokenService refreshTokenService;
    @Autowired
    private OauthCodeService codeService;
    @Autowired
    private AuthUserService userService;
    @Autowired
    private AuthenticatorManager authenticationManager;

    @Value("${xxxx.oauth2.jwt.signingKey}")
    private String jwtSignKey;

    private static final String TOKEN_TYPE = "bearer";

    public boolean verifyToken(String token) {
        return JwtUtils.verifyToken(token, jwtSignKey);
    }

    public AccessToken token(
            String clientId, String clientSecret, Collection<Scope> scope, GrantType grantType,
            Map<String, String> params) {
        OauthClientDetails clientDetails = clientDetailsService.getByClientIdAndSecret(clientId, clientSecret);
        if (null == clientDetails) throw new InvalidClientException(OauthErrorCode.INVALID_CLIENT);

        List<String> supportedGrants = Arrays.asList(clientDetails.getAuthorizedGrantTypes().split(","));
        if (!supportedGrants.contains(grantType.name().toLowerCase())) {
            throw new OauthException(OauthErrorCode.INVALID_GRANT, "invalid authorization grant");
        }
        switch (grantType) {
            case AUTHORIZATION_CODE:
                return authorizationCode(clientDetails, params.get("code"), params.get("redirect_uri"));
            case PASSWORD:
                return password(clientDetails, scope, params);
            case CLIENT_CREDENTIALS:
                return clientCredentials(clientDetails, scope);
            case REFRESH_TOKEN:
                refreshToken(clientDetails, scope, params.get("refresh_token"));
            default:
                throw new OauthException(OauthErrorCode.UNSUPPORTED_GRANT_TYPE);
        }
    }

    private AccessToken password(
            OauthClientDetails clientDetails, Collection<Scope> scope, Map<String, String> params) {
        if (CollectionUtils.isEmpty(scope)) {
            throw new OauthException(OauthErrorCode.INVALID_SCOPE, "no scope");
        }
        List<String> supportedScope = Arrays.asList(clientDetails.getScope().split(","));
        if (!supportedScope.contains(Scope.ALL.name().toUpperCase())) {
            for (Scope scopeItem : scope) {
                if (!supportedScope.contains(scopeItem.name().toLowerCase())) {
                    throw new OauthException(OauthErrorCode.INVALID_SCOPE, "invalid authorization scope");
                }
            }
        }
        AuthenticatedUser user = authenticationManager.authenticate(params);
        if (null == user) throw new OauthException(OauthErrorCode.INVALID_GRANT, "wrong username or password");
        String scopeMix = scope.stream().map(e -> e.name().toLowerCase()).collect(Collectors.joining(","));
        long iap = System.currentTimeMillis();
        ClientClaims clientClaims = ClientClaims.builder()
                .clientId(clientDetails.getClientId())
                .scope(scopeMix)
                .resource(clientDetails.getResourceIds())
                .username(user.getName())
                .iap(iap).exp(iap + clientDetails.getAccessTokenValidity() * 1000L)
                .build();
        return accessToken(clientDetails, clientClaims, user.getId(), user.getName(), iap);
    }

    private AccessToken accessToken(
            OauthClientDetails clientDetails, ClientClaims clientClaims, Long userId, String username, long iap) {
        String accessToken = JwtUtils.genToken(clientClaims, jwtSignKey);
        clientClaims.setExp(iap + clientDetails.getRefreshTokenValidity() * 1000L);
        clientClaims.setResource(null);
        clientClaims.setRefresh(true);
        String refreshToken = JwtUtils.genToken(clientClaims, jwtSignKey);
        accessTokenService.save(accessToken, DigestUtil.md5Hex(refreshToken), clientDetails.getClientId(),
                username, clientDetails.getAccessTokenValidity(), userId);
        refreshTokenService.save(refreshToken, clientDetails.getRefreshTokenValidity(), userId);

        return AccessToken.builder()
                .tokenType(TOKEN_TYPE)
                .scope(clientClaims.getScope())
                .expiresIn(clientDetails.getAccessTokenValidity())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    private AccessToken authorizationCode(OauthClientDetails clientDetails, String code, String redirectUri) {
        if (StringUtils.isEmpty(redirectUri) || !redirectUri.equals(clientDetails.getRedirectUri())) {
            throw new OauthException(OauthErrorCode.INVALID_REQUEST, "redirect_uri invalid");
        }
        OauthCode oauthCode = codeService.consumeCode(clientDetails.getClientId(), code);
        if (!redirectUri.equals(oauthCode.getRedirectUri())) throw new OauthException(OauthErrorCode.INVALID_REQUEST, "request uri invalid");
        long iap = System.currentTimeMillis();
        ClientClaims clientClaims = ClientClaims.builder()
                .clientId(clientDetails.getClientId())
                .scope(oauthCode.getScope())
                .resource(clientDetails.getResourceIds())
                .username(oauthCode.getUsername())
                .iap(iap).exp(iap + clientDetails.getAccessTokenValidity() * 1000L)
                .build();
        AuthUser user = userService.loadByUsername(oauthCode.getUsername());
        if (null == user) throw new OauthException(OauthErrorCode.INVALID_REQUEST, "username invalid");
        AccessToken accessToken = accessToken(clientDetails, clientClaims,
                user.getId(), oauthCode.getUsername(), iap);
        return accessToken;
    }

    public OauthCode oauthCode(String clientId, String redirectUri, Set<Scope> scope, String accessToken) {
        ClientClaims claims = JwtUtils.getClaimsFromAuthentication(accessToken);
        AuthUser user = userService.loadByUsername(claims.getUsername());
        if (null == user) throw new OauthException(OauthErrorCode.INVALID_REQUEST, "username invalid");
        return codeService.produceCode(clientId, redirectUri, claims.getClientId(), user.getId(), claims.getUsername(), scope);
    }

    private AccessToken clientCredentials(OauthClientDetails clientDetails, Collection<Scope> scope) {
        if (CollectionUtils.isEmpty(scope)) throw new OauthException(OauthErrorCode.INVALID_SCOPE, "no scope");
        List<String> supportedScope = Arrays.asList(clientDetails.getScope().split(","));
        for (Scope scopeItem : scope) {
            if (!supportedScope.contains(scopeItem.name().toLowerCase())) {
                throw new OauthException(OauthErrorCode.INVALID_SCOPE, "invalid authorization scope");
            }
        }
        String scopeMix = scope.stream().map(e -> e.name().toLowerCase())
                .collect(Collectors.joining(","));
        long iap = System.currentTimeMillis();
        ClientClaims clientClaims = ClientClaims.builder()
                .clientId(clientDetails.getClientId())
                .scope(scopeMix)
                .resource(clientDetails.getResourceIds())
                .role(clientDetails.getRole())
                .iap(iap).exp(iap + clientDetails.getAccessTokenValidity() * 1000L)
                .build();
        AccessToken accessToken = accessToken(clientDetails, clientClaims, ConstLiteral.Authentication.NO_ONE, null, iap);
        return accessToken;
    }

    private AccessToken refreshToken(
            OauthClientDetails clientDetails, Collection<Scope> scope, String refreshToken) {
        if (StringUtils.isEmpty(refreshToken)) {
            throw new OauthException(OauthErrorCode.INVALID_REQUEST, "refresh token invalid");
        }
        String scopeMix = null;
        if (!CollectionUtils.isEmpty(scope)) {
            List<String> supportedScope = Arrays.asList(clientDetails.getScope().split(","));
            for (Scope scopeItem : scope) {
                if (!supportedScope.contains(scopeItem.name().toLowerCase())) {
                    throw new OauthException(OauthErrorCode.INVALID_SCOPE, "invalid authorization scope");
                }
            }
            scopeMix = scope.stream().map(e -> e.name().toLowerCase())
                    .collect(Collectors.joining(","));
        }

        ClientClaims previousClientClaims;
        try {
            previousClientClaims = JwtUtils.getClaimsFromToken(refreshToken);
        } catch (BizException e) {
            throw new OauthException(OauthErrorCode.INVALID_REQUEST, "refresh token invalid");
        }
        if (null == scopeMix) {
            scopeMix = previousClientClaims.getScope();
        }
        long iap = System.currentTimeMillis();
        ClientClaims clientClaims = ClientClaims.builder()
                .clientId(clientDetails.getClientId())
                .scope(scopeMix)
                .resource(clientDetails.getResourceIds())
                .username(previousClientClaims.getUsername())
                .role(previousClientClaims.getRole())
                .iap(iap).exp(iap + clientDetails.getAccessTokenValidity() * 1000L)
                .build();
        String accessToken = JwtUtils.genToken(clientClaims, jwtSignKey);
        // refresh token 有效期不足 3 天
        boolean needRegenerateFreshToken = (previousClientClaims.getExp() - iap) < 259_200_000;
        if (needRegenerateFreshToken) {
            clientClaims.setExp(iap + clientDetails.getRefreshTokenValidity() * 1000L);
            clientClaims.setResource(null);
            clientClaims.setRefresh(true);
            refreshToken = JwtUtils.genToken(clientClaims, jwtSignKey);
        }
        long userId = ConstLiteral.Authentication.NO_ONE;
        if (StringUtils.hasText(previousClientClaims.getUsername())) {
            AuthUser user = userService.loadByUsername(previousClientClaims.getUsername());
            userId = (null != user) ? user.getId() : ConstLiteral.Authentication.NO_ONE;
        }
        accessTokenService.save(accessToken, DigestUtil.md5Hex(refreshToken), clientDetails.getClientId(),
                clientClaims.getUsername(), clientDetails.getAccessTokenValidity(), userId);
        if (needRegenerateFreshToken) {
            refreshTokenService.save(refreshToken, clientDetails.getRefreshTokenValidity(), userId);
        }

        return AccessToken.builder()
                .tokenType(TOKEN_TYPE)
                .scope(clientClaims.getScope())
                .expiresIn(clientDetails.getAccessTokenValidity())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
