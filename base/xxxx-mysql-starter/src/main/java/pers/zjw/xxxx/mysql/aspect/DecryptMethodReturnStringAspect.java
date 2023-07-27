package pers.zjw.xxxx.mysql.aspect;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import pers.zjw.xxxx.foundation.json.JsonParser;
import pers.zjw.xxxx.foundation.pojo.PageResult;
import pers.zjw.xxxx.mysql.CryptoUtils;
import pers.zjw.xxxx.mysql.annotation.Decryption;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Objects;

/**
 * 响应结果解密切面
 *
 * @author zhangjw
 * @date 2023/01/07 12:25
 */
@Slf4j
@ConditionalOnProperty("xxxx.mysql.secretKey")
@Aspect
@Component
public class DecryptMethodReturnStringAspect {
    @Value("${xxxx.mysql.secretKey}")
    private String secretKey;

    @Pointcut("@annotation(pers.zjw.xxxx.mysql.annotation.Decryption)")
    public void pointcut() {
    }

    @Around("pointcut()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();
        if (Objects.isNull(result)) return null;
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Decryption decryption = method.getAnnotation(Decryption.class);
        if (Objects.isNull(decryption)) return result;
        log.debug("begin to decrypt:{}", JsonParser.toString(result));
        result = decrypt(result, decryption);
        log.debug("decrypt result:{}", JsonParser.toString(result));
        return result;
    }

    private Object decrypt(Object object, Decryption decryption) {
        if (object instanceof Collection) {
            Collection<?> collection = (Collection<?>) object;
            if (!CollectionUtils.isEmpty(collection)) {
                Object value = collection.iterator().next();
                if (value instanceof String) {
                    return CryptoUtils.decrypt((Collection<String>)object, decryption.value(), secretKey);
                }
            }
        } else if (object instanceof PageResult) {
            PageResult<?> page = (PageResult<?>) object;
            if (!CollectionUtils.isEmpty(page.getList())) {
                Object value = page.getList().iterator().next();
                if (value instanceof String) {
                    PageResult<String> exactPage = (PageResult<String>) object;
                    exactPage.setList(CryptoUtils.decrypt(exactPage.getList(), decryption.value(), secretKey));
                    return exactPage;
                }
            }
        } else if (object instanceof IPage) {
            IPage<?> page = (IPage<?>) object;
            if (!CollectionUtils.isEmpty(page.getRecords())) {
                Object value = page.getRecords().iterator().next();
                if (value instanceof String) {
                    IPage<String> exactPage = (IPage<String>) object;
                    exactPage.setRecords(CollectionUtil.list(true,
                            CryptoUtils.decrypt(exactPage.getRecords(), decryption.value(), secretKey)));
                    return exactPage;
                }
            }
        } else if (object.getClass().isArray()) {
            if (object.getClass().getComponentType().equals(String.class)) {
                return CryptoUtils.decrypt((String[])object, decryption.value(), secretKey);
            }
        } else if (object instanceof String){
            return CryptoUtils.decrypt((String)object, decryption.value(), secretKey);
        }
        return object;
    }
}
