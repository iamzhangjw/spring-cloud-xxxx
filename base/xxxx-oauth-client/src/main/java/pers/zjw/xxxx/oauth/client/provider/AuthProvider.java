package pers.zjw.xxxx.oauth.client.provider;

import pers.zjw.xxxx.web.pojo.AuthenticatedUser;
import pers.zjw.xxxx.web.pojo.WebResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * auth feign client
 *
 * @author zhangjw
 * @date 2022/12/24 0024 17:28
 */
@FeignClient(name = "xxxx-oauth")
public interface AuthProvider {
    /**
     * 调用签权服务，判断用户是否有权限
     */
    @GetMapping(value = "/auth/hasPermission")
    WebResponse<Boolean> hasPermission(
            @RequestParam(value = "service", required = false) String service,
            @RequestParam("uri") String uri,
            @RequestParam("method") String method);

    /**
     * 查询授权登录的用户
     */
    @GetMapping("/auth/authenticatedUser")
    WebResponse<AuthenticatedUser> authenticatedUser();
}
