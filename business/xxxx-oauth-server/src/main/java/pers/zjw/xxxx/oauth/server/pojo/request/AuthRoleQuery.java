package pers.zjw.xxxx.oauth.server.pojo.request;

import lombok.Data;

/**
 * AuthRoleQuery
 *
 * @author zhangjw
 * @date 2023/01/05 23:52
 */
@Data
public class AuthRoleQuery {
    /**
     * 角色名称
     */
    private String name;

    /**
     * 角色编码
     */
    private String code;

    /**
     * 启用标记
     */
    private Boolean enabled;
}
