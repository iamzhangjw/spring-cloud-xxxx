package pers.zjw.xxxx.web.pojo;

import pers.zjw.xxxx.web.constant.ConstLiteral;
import lombok.Data;

import java.io.Serializable;

/**
 * 认证授权成功的用户信息
 *
 * @author zhangjw
 * @date 2022/12/30 0030 10:24
 */
@Data
public class AuthenticatedUser implements Serializable {
    private static final long serialVersionUID = 8691505721985615395L;

    public static final AuthenticatedUser NO_ONE = new AuthenticatedUser();

    private Long id = ConstLiteral.Authentication.NO_ONE;
    /**
     * 登录用户名
     */
    private String name;

    /**
     * 用户姓名
     */
    private String nickname;

    /**
     * 用户手机
     */
    private String mobile;
}
