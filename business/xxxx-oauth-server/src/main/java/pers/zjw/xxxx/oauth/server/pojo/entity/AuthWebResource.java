package pers.zjw.xxxx.oauth.server.pojo.entity;

import pers.zjw.xxxx.mysql.pojo.entity.GenericEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * web资源表
 * </p>
 *
 * @author zhangjw
 * @since 2022-10-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AuthWebResource extends GenericEntity {
    private static final long serialVersionUID = 7977406391942633727L;
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
     * 资源路径，记录上溯至根资源id的访问路径
     */
    private String path;

    /**
     * 序号，用于同级资源排序
     */
    private Integer orderNum;

    /**
     * 启用标记
     */
    private Boolean enabled;
}
