package pers.zjw.xxxx.oauth.server.pojo.entity;

import pers.zjw.xxxx.mysql.pojo.entity.GenericEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 角色表
 * </p>
 *
 * @author zhangjw
 * @since 2023-04-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AuthRole extends GenericEntity {
    private static final long serialVersionUID = 2005699514440332234L;
    /**
     * 角色名称
     */
    private String name;

    /**
     * 角色编码
     */
    private String code;

    /**
     * 描述
     */
    private String description;

    /**
     * 启用标记
     */
    private Boolean enabled;
}
