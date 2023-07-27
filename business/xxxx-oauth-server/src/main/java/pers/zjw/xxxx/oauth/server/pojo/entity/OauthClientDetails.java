package pers.zjw.xxxx.oauth.server.pojo.entity;

import pers.zjw.xxxx.mysql.pojo.entity.GenericEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * oauth客户端信息表
 * </p>
 *
 * @author zhangjw
 * @since 2022-09-26
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OauthClientDetails extends GenericEntity {
    private static final long serialVersionUID = -7669021821666177447L;
    /**
     * 客户端id，用于唯一标识每一个客户端(client)
     */
    private String clientId;

    /**
     * 客户端所能访问的资源id集合,多个资源时用逗号分隔
     */
    private String resourceIds;

    /**
     * 用于指定客户端(client)的访问密钥
     */
    private String clientSecret;

    /**
     * 指定客户端申请的权限范围,可选值包括read,write;若有多个权限范围用逗号分隔
     */
    private String scope;

    /**
     * 指定客户端支持的grant_type,可选值包括authorization_code,password,refresh_token,implicit,client_credentials, 若支持多个grant_type用逗号(,)分隔
     */
    private String authorizedGrantTypes;

    /**
     * 客户端的重定向URI,可为空
     */
    private String redirectUri;

    /**
     * 客户端拥有的角色,可用于第三方客户端访问API限制
     */
    private String role;

    /**
     * 设定客户端的access_token的有效时间值(单位:秒),可选, 若不设定值则使用默认的有效时间值(60 * 60 * 12, 12小时).
     */
    private Integer accessTokenValidity;

    /**
     * 设定客户端的refresh_token的有效时间值(单位:秒),可选, 若不设定值则使用默认的有效时间值(60 * 60 * 24 * 30, 30天).
     */
    private Integer refreshTokenValidity;

    /**
     * 附加信息，预留的字段没有实际的使用,可选,但若设置值,必须是JSON格式的数据
     */
    private String additionalInformation;
}
