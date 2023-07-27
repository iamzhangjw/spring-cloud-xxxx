package pers.zjw.xxxx.oauth.server.exception;

import pers.zjw.xxxx.foundation.exception.GenericException;
import pers.zjw.xxxx.oauth.server.constant.OauthErrorCode;
import org.springframework.util.StringUtils;

/**
 * write some class description
 *
 * @author zhangjw
 * @date 2023/12/26 0026 19:18
 */
public class OauthException extends GenericException {
    private static final long serialVersionUID = 580805897333664116L;

    private final OauthErrorCode code;
    private String msg;

    public OauthException(OauthErrorCode code) {
        this.code = code;
    }

    public OauthException(OauthErrorCode code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String code() {
        return code.name().toLowerCase();
    }

    @Override
    public String getMessage() {
        return StringUtils.hasText(msg) ? msg : code.description();
    }
}
