package pers.zjw.xxxx.mysql.annotation;

import pers.zjw.xxxx.mysql.CryptoAlgorithm;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 解密注解
 *
 * 加载方法或者字段上，表示需要解密
 *
 * @author zhangjw
 * @date 2023/01/15 0015 9:34
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface Decryption {
    CryptoAlgorithm value() default CryptoAlgorithm.QUERY;
}
