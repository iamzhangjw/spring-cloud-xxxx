package pers.zjw.xxxx.oauth.server.pojo.request;

import lombok.Data;

/**
 * AuthRoleForm
 *
 * @author zhangjw
 * @date 2023/01/05 23:52
 */
@Data
public class AuthRoleForm {
    private Long id;
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
