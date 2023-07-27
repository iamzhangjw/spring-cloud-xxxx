package pers.zjw.xxxx.oauth.server.pojo.entity;

import pers.zjw.xxxx.mysql.pojo.entity.GenericEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * oauth授权码表
 * </p>
 *
 * @author zhangjw
 * @since 2022-09-26
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OauthCode extends GenericEntity {
    private static final long serialVersionUID = 3854978694735625454L;
    /**
     * 授权码，有效期为5分钟
     */
    private String code;

    private String applyClientId;

    private String redirectUri;

    /**
     * 客户端id
     */
    private String clientId;

    private Long userId;

    /**
     * 登录的用户名
     */
    private String username;

    /**
     * 申请的权限
     */
    private String scope;

    /**
     * 有效时长（s）
     */
    private Integer expiresIn;
}
