package pers.zjw.xxxx.oauth.server.pojo.entity;

import pers.zjw.xxxx.mysql.pojo.entity.GenericEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * web和api资源关系表
 * </p>
 *
 * @author zhangjw
 * @since 2022-10-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AuthWebApiResourceRelation extends GenericEntity {
    private static final long serialVersionUID = -3151451555475448891L;
    /**
     * web资源id
     */
    private Long webResourceId;

    /**
     * api资源id
     */
    private Long apiResourceId;
}
