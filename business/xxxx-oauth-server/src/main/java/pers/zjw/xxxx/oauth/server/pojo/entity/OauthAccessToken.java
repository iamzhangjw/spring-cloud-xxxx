package pers.zjw.xxxx.oauth.server.pojo.entity;

import pers.zjw.xxxx.mysql.pojo.entity.GenericEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * oauth客户access_token表
 * </p>
 *
 * @author zhangjw
 * @since 2022-09-26
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OauthAccessToken extends GenericEntity {
    private static final long serialVersionUID = -6617630077399436958L;
    /**
     * 该字段具有唯一性, 是是将access_token的值通过MD5加密生成的
     */
    private String tokenId;

    /**
     * access_token内容
     */
    private String token;

    /**
     * 登录用户名
     */
    private String userName;

    /**
     * 客户端id
     */
    private String clientId;

    /**
     * 该字段的值是将refresh_token的值通过MD5加密后存储的
     */
    private String refreshToken;

    private Integer expiresIn;
}
