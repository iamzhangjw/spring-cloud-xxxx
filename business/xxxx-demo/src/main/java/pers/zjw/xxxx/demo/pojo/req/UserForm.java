package pers.zjw.xxxx.demo.pojo.req;

import lombok.Data;

/**
 * UserForm
 *
 * @author zhangjw
 * @date 2022/10/22 0022 15:34
 */
@Data
public class UserForm {
    private String name;

    /**
     * 电话
     */
    private String mobile;

    /**
     * 证件号码
     */
    private String certificationNo;

    /**
     * 邮箱地址
     */
    private String email;
}
