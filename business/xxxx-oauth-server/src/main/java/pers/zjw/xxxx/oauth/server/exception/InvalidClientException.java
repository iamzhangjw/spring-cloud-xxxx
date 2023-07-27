package pers.zjw.xxxx.oauth.server.exception;

import pers.zjw.xxxx.oauth.server.constant.OauthErrorCode;

/**
 * invalid client exception
 *
 * @author zhangjw
 * @date 2023/12/26 0026 19:22
 */
public class InvalidClientException extends OauthException {

    public InvalidClientException(OauthErrorCode errorCode) {
        super(errorCode);
    }

    public InvalidClientException(OauthErrorCode errorCode, String msg) {
        super(errorCode, msg);
    }
}
