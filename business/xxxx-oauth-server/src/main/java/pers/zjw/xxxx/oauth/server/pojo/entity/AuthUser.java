package pers.zjw.xxxx.oauth.server.pojo.entity;

import pers.zjw.xxxx.mysql.pojo.entity.GenericEntity;
import pers.zjw.xxxx.web.pojo.AuthenticatedUser;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author zhangjw
 * @since 2022-09-27
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AuthUser extends GenericEntity {
    private static final long serialVersionUID = 930651748957080177L;
    /**
     * 登录用户名(不可更改)
     */
    private String name;

    /**
     * 用户密码
     */
    private String password;

    /**
     * 用户姓名
     */
    private String nickname;

    /**
     * 用户手机
     */
    private String mobile;

    /**
     * 上次登录时间戳
     */
    private Long lastLoginTime;

    private Boolean enabled;
    private Boolean locked;

    public AuthenticatedUser to () {
        AuthenticatedUser authenticatedUser = new AuthenticatedUser();
        authenticatedUser.setId(this.getId());
        authenticatedUser.setName(this.getName());
        authenticatedUser.setNickname(this.getNickname());
        authenticatedUser.setMobile(this.getMobile());
        return authenticatedUser;
    }
}
