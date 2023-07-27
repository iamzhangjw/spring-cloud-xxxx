package pers.zjw.xxxx.oauth.client.context;

import java.lang.annotation.*;

/**
 * 当前登录用户注解
 *
 * @author zhangjw
 * @date 2022/12/30 0030 10:27
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CurrentUser {
}
