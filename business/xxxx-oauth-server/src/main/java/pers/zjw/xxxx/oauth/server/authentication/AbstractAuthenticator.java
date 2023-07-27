package pers.zjw.xxxx.oauth.server.authentication;

import pers.zjw.xxxx.oauth.server.constant.OauthErrorCode;
import pers.zjw.xxxx.oauth.server.exception.OauthException;
import pers.zjw.xxxx.web.pojo.AuthenticatedUser;

import java.util.Map;
import java.util.Objects;

/**
 * AbstractAuthenticator
 *
 * @author zhangjw
 * @date 2023/02/13 18:04
 */
abstract class AbstractAuthenticator implements Authenticator {
    private Authenticator next;

    final AuthenticatedUser doNext(Map<String, String> params) {
        if (Objects.nonNull(next)) return next.authenticate(params);
        throw new OauthException(OauthErrorCode.INVALID_CLIENT, "凭证验证失败");
    }

    final void setNext(Authenticator next) {
        this.next = next;
    }
}
