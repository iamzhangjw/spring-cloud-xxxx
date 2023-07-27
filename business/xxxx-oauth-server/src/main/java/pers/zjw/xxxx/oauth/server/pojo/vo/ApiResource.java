package pers.zjw.xxxx.oauth.server.pojo.vo;

import lombok.Getter;

import java.io.Serializable;

/**
 * api resource
 *
 * @author zhangjw
 * @date 2023/01/05 10:51
 */
@Getter
public class ApiResource implements Serializable {
    private static final long serialVersionUID = 1815766781713496293L;
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

    public ApiResource(String service, String uri, String method) {
        this.service = service;
        this.uri = uri;
        this.method = method;
    }
}
