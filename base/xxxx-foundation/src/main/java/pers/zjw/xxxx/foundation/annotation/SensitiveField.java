package pers.zjw.xxxx.foundation.annotation;

import pers.zjw.xxxx.foundation.constant.AccountLevel;
import pers.zjw.xxxx.foundation.constant.FieldType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 脱敏注解
 *
 * @author zhangjw
 * @date 2023/01/12 0012 15:46
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface SensitiveField {
    FieldType value() default FieldType.DEFAULT;
    AccountLevel level() default AccountLevel.ZERO;
}
