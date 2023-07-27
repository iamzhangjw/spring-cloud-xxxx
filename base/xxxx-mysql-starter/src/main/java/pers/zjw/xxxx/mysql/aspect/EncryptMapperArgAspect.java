package pers.zjw.xxxx.mysql.aspect;

import cn.hutool.core.util.ArrayUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import pers.zjw.xxxx.foundation.json.JsonParser;
import pers.zjw.xxxx.mysql.CryptoUtils;
import pers.zjw.xxxx.mysql.EncryptionArgsHolder;
import pers.zjw.xxxx.mysql.annotation.Encryption;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * 参数加密切面
 *
 * @author zhangjw
 * @date 2023/01/07 12:25
 */
@Slf4j
@ConditionalOnProperty("xxxx.mysql.secretKey")
@Aspect
@Component
public class EncryptMapperArgAspect {
    @Value("${xxxx.mysql.secretKey}")
    private String secretKey;

    @Pointcut("target(com.baomidou.mybatisplus.core.mapper.BaseMapper)")
    public void pointcut() {
    }

    @Before("pointcut()")
    public void doBefore(JoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        if (ArrayUtil.isEmpty(args)) return;
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Annotation[][] annotations = method.getParameterAnnotations();
        EncryptionArgsHolder.getInstance().setContext(new LinkedHashMap<>());
        for (int i=0; i<args.length; i++) {
            if ((null == args[i]) || (args[i] instanceof Wrapper) || (args[i] instanceof Page)) continue;
            log.debug("begin to encrypt arg[{}]:{}", args[i].getClass(), JsonParser.toString(args[i]));
            encrypt(args[i], annotations[i]);
            log.debug("encrypt arg result:{}", JsonParser.toString(args[i]));
        }
    }

    private void encrypt(Object object, Annotation[] annotation) throws IllegalAccessException {
        Encryption encryption = null;
        Param param = null;
        for (Annotation ann : annotation) {
            if (ann instanceof Encryption) {
                encryption = (Encryption) ann;
            }
            if (ann instanceof Param) {
                param = (Param) ann;
            }
        }
        Map<String, String> encryptArgs = EncryptionArgsHolder.getInstance().getContext();
        if (object instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) object;
            if (CollectionUtils.isEmpty(map) || null == encryption) return;
            if (null != param) {
                CryptoUtils.encrypt(map, secretKey);
            } else {
                for (Map.Entry<?, ?> entry : map.entrySet()) {
                    Object key = entry.getKey(), value = entry.getValue();
                    if (key instanceof String && value instanceof String) {
                        encryptArgs.put((String)entry.getKey(),
                                CryptoUtils.encrypt((String)value, encryption.value(), secretKey));
                    }
                }
            }
        } else if (object instanceof Collection) {
            Collection<Object> collection = (Collection<Object>) object;
            if (CollectionUtils.isEmpty(collection) || null == encryption) return;
            Collection<Object> encryptedCollection = new LinkedList<>();
            for (Object e : collection) {
                if (e instanceof String) {
                    encryptedCollection.add(CryptoUtils.encrypt((String)e, encryption.value(), secretKey));
                } else {
                    CryptoUtils.encrypt(e, secretKey);
                    encryptedCollection.add(e);
                }
            }
            try {
                collection.clear();
                collection.addAll(encryptedCollection);
            } catch (Exception e) {
                log.error("replace plain arg in collection to cipher failed.", e);
            }
        } else if (object.getClass().isArray()) {
            Object[] array = (Object[])object;
            if (ArrayUtil.isEmpty(array) || null == encryption) return;
            if (object.getClass().getComponentType().equals(String.class)) {
                String[] strArr = (String[])object;
                String[] enStrArr =  CryptoUtils.encrypt(strArr, encryption.value(), secretKey);
                for (int i=0; i< strArr.length; i++) {
                    strArr[i] = enStrArr[i];
                }
            } else {
                CryptoUtils.encrypt((Object[])object, secretKey);
            }
        } else if (object instanceof String) {
            String str = (String) object;
            if (StringUtils.isEmpty(str) || null == encryption || null == param) return;
            encryptArgs.put(param.value(), CryptoUtils.encrypt((String)object, encryption.value(), secretKey));
        } else {
            CryptoUtils.encrypt(object, secretKey);
        }
    }
}
