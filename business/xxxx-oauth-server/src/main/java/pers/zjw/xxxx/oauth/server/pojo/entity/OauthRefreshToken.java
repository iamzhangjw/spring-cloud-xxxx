package pers.zjw.xxxx.oauth.server.pojo.entity;

import pers.zjw.xxxx.mysql.pojo.entity.GenericEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * oauth客户refresh_token表
 * </p>
 *
 * @author zhangjw
 * @since 2022-09-26
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OauthRefreshToken extends GenericEntity {
    private static final long serialVersionUID = -7089247474941525259L;
    /**
     * 该字段具有唯一性, 是是将refresh_token的值通过MD5加密生成的
     */
    private String tokenId;

    /**
     * refresh_token内容
     */
    private String token;

    private Integer expiresIn;
}
