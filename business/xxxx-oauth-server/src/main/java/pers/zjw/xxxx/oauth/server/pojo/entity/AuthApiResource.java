package pers.zjw.xxxx.oauth.server.pojo.entity;

import pers.zjw.xxxx.mysql.pojo.entity.GenericEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * api资源表
 * </p>
 *
 * @author zhangjw
 * @since 2023-04-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AuthApiResource extends GenericEntity {
    private static final long serialVersionUID = -6789623066847850561L;
    /**
     * 资源名称
     */
    private String name;

    /**
     * 资源所属服务
     */
    private String service;

    /**
     * 访问 uri
     */
    private String uri;

    /**
     * api 请求方法，包括 GET, POST, DELETE 等
     */
    private String method;

    /**
     * 是否排除权限校验
     */
    private Boolean exclude;
}
