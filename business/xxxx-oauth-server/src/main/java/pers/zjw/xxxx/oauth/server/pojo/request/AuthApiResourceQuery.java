package pers.zjw.xxxx.oauth.server.pojo.request;

import lombok.Data;

/**
 * AuthApiResourceReq
 * @author zhangjw
 * @since 2023-01-04
 */
@Data
public class AuthApiResourceQuery {
    /**
     * 资源名称
     */
    private String name;

    /**
     * 资源所属服务
     */
    private String service;

    /**
     * 访问uri
     */
    private String uri;

    /**
     * api请求方法，包括 GET,POST,DELETE等
     */
    private String method;

    /**
     * 是否排除权限校验
     */
    private Boolean exclude;
}
