package pers.zjw.xxxx.oauth.server.pojo.vo;

import pers.zjw.xxxx.oauth.server.pojo.entity.AuthWebResource;
import lombok.Data;

import java.util.Set;

/**
 * web resource
 *
 * @author zhangjw
 * @date 2023/01/06 15:33
 */
@Data
public class WebResource {
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

    private Boolean enabled;

    private Set<WebResource> leaves;

    public static WebResource of(AuthWebResource source) {
        WebResource target = new WebResource();
        target.setId(source.getId());
        target.setName(source.getName());
        target.setDisplayName(source.getDisplayName());
        target.setCode(source.getCode());
        target.setUri(source.getUri());
        target.setType(source.getType());
        target.setParentId(source.getParentId());
        target.setOrderNum(source.getOrderNum());
        target.setEnabled(source.getEnabled());
        return target;
    }
}
