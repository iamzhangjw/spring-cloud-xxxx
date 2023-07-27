package pers.zjw.xxxx.oauth.server.pojo.entity;

import pers.zjw.xxxx.mysql.pojo.entity.GenericEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 角色和web资源关系表
 * </p>
 *
 * @author zhangjw
 * @since 2023-01-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AuthRoleWebResourceRelation extends GenericEntity {
    private static final long serialVersionUID = 509202762620459795L;
    /**
     * 角色id
     */
    private Long roleId;

    /**
     * web资源id
     */
    private Long resourceId;
}
