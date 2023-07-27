package pers.zjw.xxxx.oauth.server.pojo.request;

import lombok.Data;

/**
 * AuthWebResourceForm
 *
 * @author zhangjw
 * @date 2023/01/06 15:04
 */
@Data
public class AuthWebResourceForm {
    private Long id;
    /**
     * 资源名称
     */
    private String name;

    /**
     * 资源显示名称
     */
    private String displayName;

    /**
     * 资源编码
     */
    private String code;

    /**
     * 访问uri
     */
    private String uri;

    /**
     * 资源类型，MODULE-模块，MENU-菜单，BUTTON-按钮，ELEMENT-元素
     */
    private String type;

    /**
     * 父资源id
     */
    private Long parentId;

    /**
     * 序号，用于同级资源排序
     */
    private Integer orderNum;

    /**
     * 启用标记
     */
    private Boolean enabled;
}
