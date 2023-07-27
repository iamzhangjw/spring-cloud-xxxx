package pers.zjw.xxxx.gateway.provider;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * demp provider
 *
 * @author zhangjw
 * @date 2023/05/28 0028 17:50
 */
@FeignClient("xxxx-demo")
public interface DemoProvider {
    @GetMapping("/hello")
    String hello();
}
