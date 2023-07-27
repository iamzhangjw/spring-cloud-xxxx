package pers.zjw.xxxx.web.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 当前登录客户端信息
 *
 * @author zhangjw
 * @date 2022/12/24 0024 16:33
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientClaims implements Serializable {
    private static final long serialVersionUID = 8280604122797837975L;

    public static final ClientClaims EMPTY = ClientClaims.builder().build();

    /**
     * client id
     */
    private String clientId;
    /**
     * approval scopes
     */
    private String scope;
    /**
     * accessible resources
     */
    private String resource;
    /**
     * token 签发时间
     */
    private Long iap;
    /**
     * token 失效时间
     */
    private Long exp;

    private String username;
    private String role;
    private Boolean refresh = false;
}
