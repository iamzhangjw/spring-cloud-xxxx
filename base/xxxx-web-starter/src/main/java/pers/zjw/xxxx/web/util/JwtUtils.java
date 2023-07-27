package pers.zjw.xxxx.web.util;

import cn.hutool.crypto.digest.HmacAlgorithm;
import cn.hutool.json.JSONUtil;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTPayload;
import cn.hutool.jwt.JWTUtil;
import cn.hutool.jwt.signers.JWTSignerUtil;
import pers.zjw.xxxx.foundation.json.JsonParser;
import pers.zjw.xxxx.web.constant.ErrorCode;
import pers.zjw.xxxx.web.exception.BizException;
import pers.zjw.xxxx.web.pojo.ClientClaims;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

/**
 * jwt util
 *
 * @author zhangjw
 * @date 2022/12/25 18:04
 */
public class JwtUtils {
    /**
     * Authorization认证开头是"bearer "
     */
    public static final String BEARER = "Bearer ";

    /**
     * 生成 jwt token
     * @param claims 当前登录用户
     * @param signKey 签名密钥
     * @return
     */
    public static String genToken(ClientClaims claims, String signKey) {
        return genToken(JsonParser.toMap(claims), signKey);
    }

    private static String genToken(Map<String, Object> claims, String signKey) {
        JWT jwt = JWT.create();
        claims.entrySet().stream()
                .filter(e -> StringUtils.hasText(e.getKey()) && Objects.nonNull(e.getValue()))
                .forEach(e -> jwt.setPayload(e.getKey(),e.getValue()));
        return jwt.setSigner(HmacAlgorithm.HmacSHA512.getValue(), signKey.getBytes(StandardCharsets.UTF_8))
                .sign();
    }

    public static ClientClaims getClaimsFromAuthentication(String authentication) {
        return getClaimsFromAuthentication(authentication, null);
    }

    public static ClientClaims getClaimsFromAuthentication(String authentication, String signKey) {
        String token = authentication;
        if (authentication.startsWith(BEARER)) {
            token = authentication.substring(BEARER.length());
        }
        return getClaimsFromToken(token, signKey);
    }

    public static ClientClaims getClaimsFromToken(String token) {
        return getClaimsFromToken(token, null);
    }

    public static ClientClaims getClaimsFromToken(String token, String signKey) {
        JWTPayload payload = getPayloadFromToken(token, signKey);
        return JSONUtil.toBean(payload.getClaimsJson(), ClientClaims.class);
    }

    public static String getPayloadStringFromAuthentication(String authentication) {
        return getPayloadStringFromAuthentication(authentication, null);
    }

    public static String getPayloadStringFromAuthentication(String authentication, String signKey) {
        String token = authentication;
        if (authentication.startsWith(BEARER)) {
            token = authentication.substring(BEARER.length());
        }
        return getPayloadStringFromToken(token, signKey);
    }

    public static String getPayloadStringFromToken(String token) {
        return getPayloadStringFromToken(token, null);
    }

    public static String getPayloadStringFromToken(String token, String signKey) {
        JWTPayload payload = getPayloadFromToken(token, signKey);
        return payload.toString();
    }

    private static JWTPayload getPayloadFromToken(String token, String signKey) {
        JWT jwt = JWTUtil.parseToken(token);
        if (StringUtils.hasText(signKey) && !jwt.verify(JWTSignerUtil.hs512(signKey.getBytes(StandardCharsets.UTF_8)))) {
            throw new BizException(ErrorCode.WRONG_KEY);
        }
        Long expiredAt = (Long) jwt.getPayload("exp");
        if (expiredAt < System.currentTimeMillis()) {
            throw new BizException(ErrorCode.TOKEN_INVALID_OR_EXPIRED);
        }
        return jwt.getPayload();
    }

    public static boolean verifyToken(String token) {
        return verifyToken(token, null);
    }

    public static boolean verifyToken(String token, String signKey) {
        if (token.startsWith(BEARER)) {
            token = token.substring(BEARER.length());
        }
        JWT jwt = JWTUtil.parseToken(token);
        if (StringUtils.hasText(signKey)) {
            if (!jwt.verify(JWTSignerUtil.hs512(signKey.getBytes(StandardCharsets.UTF_8)))) return false;
        }
        Long expiredAt = (Long) jwt.getPayload("exp");
        if (expiredAt < System.currentTimeMillis()) return false;
        return true;
    }
}