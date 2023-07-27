package pers.zjw.xxxx.websocket.handler;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * web socket uri 注解
 *
 * @author zhangjw
 * @date 2023/01/15 0015 9:43
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Endpoint {
    String value();
}
