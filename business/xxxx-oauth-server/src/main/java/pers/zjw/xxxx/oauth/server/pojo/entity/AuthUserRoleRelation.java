package pers.zjw.xxxx.oauth.server.pojo.entity;

import pers.zjw.xxxx.mysql.pojo.entity.GenericEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 用户和角色关系表
 * </p>
 *
 * @author zhangjw
 * @since 2022-10-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AuthUserRoleRelation extends GenericEntity {
    private static final long serialVersionUID = 2969725928077049088L;
    /**
     * 用户id
     */
    private Long userId;

    /**
     * 角色id
     */
    private Long roleId;
}
