package pers.zjw.xxxx.mysql.annotation;


import pers.zjw.xxxx.mysql.CryptoAlgorithm;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 加解密字段注解
 * 加在类上，表示这个类在和数据库交互时需要加/解密
 * 加在字段上，表示这个字段需要加/解密
 *
 * @author zhangjw
 * @date 2023/01/15 0015 9:43
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface Crypto {
    CryptoAlgorithm value() default CryptoAlgorithm.QUERY;
    @AliasFor("value")
    CryptoAlgorithm algorithm() default CryptoAlgorithm.QUERY;
}
