package pers.zjw.xxxx.oauth.server.authentication;

import pers.zjw.xxxx.oauth.server.pojo.entity.AuthUser;
import pers.zjw.xxxx.oauth.server.service.AuthUserService;
import pers.zjw.xxxx.web.pojo.AuthenticatedUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * UsernamePasswordAuthenticator
 *
 * @author zhangjw
 * @date 2023/01/06 19:46
 */
@Order(0)
@Component
class UsernamePasswordAuthenticator extends AbstractAuthenticator {
    @Autowired
    private AuthUserService userService;

    @Override
    public AuthenticatedUser authenticate(Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            return null;
        }
        AuthUser user = userService.loadByUsername(username);
        if (null != user && password.equals(user.getPassword())) {
            return user.to();
        }
        return doNext(params);
    }
}
