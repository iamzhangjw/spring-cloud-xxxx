package pers.zjw.xxxx.oauth.server.authentication;

import pers.zjw.xxxx.web.pojo.AuthenticatedUser;

import java.util.Map;

/**
 * Authenticator
 *
 * @author zhangjw
 * @date 2023/01/06 19:05
 */
interface Authenticator {
    AuthenticatedUser authenticate(Map<String, String> params);
}
