package pers.zjw.xxxx.mysql.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;

/**
 * generic entity
 *
 * @author zhangjw
 * @date 2022/12/1 0001 17:26
 */
@Data
public class GenericEntity implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 创建时间戳
     */
    @OrderBy
    //@TableField(fill = FieldFill.INSERT)
    private Long createAt;
    /**
     * 创建人
     */
    //@TableField(fill = FieldFill.INSERT)
    private Long createBy;
    /**
     * 修改时间戳
     */
    //@TableField(fill = FieldFill.INSERT_UPDATE)
    private Long modifyAt;
    /**
     * 修改人
     */
    //@TableField(fill = FieldFill.INSERT_UPDATE)
    private Long modifyBy;
    /**
     * 删除标记
     */
    @TableLogic
    private Boolean deleted = false;

    /**
     * 版本
     */
    //@Version
    private Long version;
}
