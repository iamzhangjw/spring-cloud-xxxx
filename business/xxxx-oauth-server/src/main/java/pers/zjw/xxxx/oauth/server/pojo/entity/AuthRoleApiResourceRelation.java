package pers.zjw.xxxx.oauth.server.pojo.entity;

import pers.zjw.xxxx.mysql.pojo.entity.GenericEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 角色和api资源关系表
 * </p>
 *
 * @author zhangjw
 * @since 2023-04-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AuthRoleApiResourceRelation extends GenericEntity {
    private static final long serialVersionUID = -2544050664081950448L;
    /**
     * 角色id
     */
    private Long roleId;

    /**
     * api资源id
     */
    private Long resourceId;
}
