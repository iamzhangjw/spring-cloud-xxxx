package pers.zjw.xxxx.demo.pojo.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import pers.zjw.xxxx.mysql.pojo.entity.GenericEntity;

/**
 * <p>
 * 字典表
 * </p>
 *
 * @author zhangjw
 * @since 2023-05-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Dict extends GenericEntity {

    private static final long serialVersionUID = 1L;

    /**
     * code
     */
    private String code;

    /**
     * value
     */
    private String value;
}
