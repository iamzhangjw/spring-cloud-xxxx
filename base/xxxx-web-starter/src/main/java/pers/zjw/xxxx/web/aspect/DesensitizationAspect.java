package pers.zjw.xxxx.web.aspect;

import pers.zjw.xxxx.foundation.json.JsonParser;
import pers.zjw.xxxx.foundation.pojo.PageResult;
import pers.zjw.xxxx.foundation.toolkit.DesensitizationUtils;
import pers.zjw.xxxx.web.pojo.WebResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Map;

/**
 * 脱敏切面
 *
 * @author zhangjw
 * @date 2023/01/07 12:25
 */
@Slf4j
@Aspect
@Component
public class DesensitizationAspect {
    @Pointcut("@annotation(pers.zjw.xxxx.foundation.annotation.Desensitization)")
    public void pointcut() {
    }

    @Around("pointcut()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();
        log.debug("begin to desensitize:{}", JsonParser.toString(result));
        if (result instanceof Map) {
            DesensitizationUtils.blur((Map<?, ?>) result);
        } else if (result instanceof Collection) {
            DesensitizationUtils.blur((Collection<?>) result);
        } else if (result.getClass().isArray()) {
            DesensitizationUtils.blur((Object[]) result);
        } else if (result instanceof WebResponse) {
            WebResponse<?> response = (WebResponse<?>) result;
            if (response.isSuccess()) {
                Object o = response.getData();
                if (o instanceof Map) {
                    DesensitizationUtils.blur((Map<?, ?>) o);
                } else if (o instanceof Collection) {
                    DesensitizationUtils.blur((Collection<?>) o);
                } else if (o.getClass().isArray()) {
                    DesensitizationUtils.blur((Object[]) o);
                } else {
                    DesensitizationUtils.blur(o);
                }
            }
        } else if (result instanceof PageResult) {
            PageResult<?> page = (PageResult<?>) result;
            if (!CollectionUtils.isEmpty(page.getList())) {
                DesensitizationUtils.blur(page.getList());
            }
        } else {
            DesensitizationUtils.blur(result);
        }
        log.debug("desensitize result:{}", JsonParser.toString(result));
        return result;
    }
}
