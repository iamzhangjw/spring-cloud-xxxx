package pers.zjw.xxxx.oauth.server.controller;

import pers.zjw.xxxx.oauth.server.service.AuthorizationService;
import pers.zjw.xxxx.web.controller.BaseController;
import pers.zjw.xxxx.web.pojo.AuthenticatedUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * authorization controller
 *
 * @author zhangjw
 * @date 2022/12/27 0027 15:04
 */
@Slf4j
@RequestMapping("/auth")
@RestController
public class AuthorizationController extends BaseController {
    @Autowired
    private AuthorizationService authorizationService;

    @GetMapping("/hasPermission")
    public Boolean hasPermission(
            @RequestParam(value = "service", required = false) String service, @RequestParam("uri") String uri,
            @RequestParam("method") String method) {
        log.debug("check api resource permission {} {} {}", service, uri, method);
        return authorizationService.hasPermission(service, uri, method);
    }

    @GetMapping("/authenticatedUser")
    public AuthenticatedUser authenticatedUser() {
        return authorizationService.authenticatedUser();
    }
}
