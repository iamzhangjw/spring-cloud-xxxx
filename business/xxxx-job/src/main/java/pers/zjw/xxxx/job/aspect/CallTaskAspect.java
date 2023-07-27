package pers.zjw.xxxx.job.aspect;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

/**
 * CallTaskAspect
 *
 * @author zhangjw
 * @date 2023/06/07 12:25
 */
@Slf4j
@Aspect
@Component
public class CallTaskAspect {
    @Pointcut("within(pers.zjw.xxxx.job..*) && @annotation(org.springframework.scheduling.annotation.Scheduled)")
    public void pointcut() { }

    @Before("pointcut()")
    public void doBefore(JoinPoint joinPoint) {
    }

    @AfterReturning(pointcut = "pointcut()", returning = "ret")
    public void doAfterReturning(JoinPoint joinPoint, Object ret) {
        // TODO 记录结果
        log.debug("call task {} success, result:{}", joinPoint.getSignature(), ret);
    }

    @JsonProperty
    @AfterThrowing(pointcut = "pointcut()", throwing = "th")
    public void doAround(JoinPoint joinPoint, Throwable th) {
        log.warn("call task {} failed:", joinPoint.getSignature(), th);
        // TODO 发送邮件，记录结果
    }
}
