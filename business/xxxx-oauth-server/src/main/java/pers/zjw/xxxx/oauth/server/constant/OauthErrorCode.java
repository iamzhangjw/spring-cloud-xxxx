package pers.zjw.xxxx.oauth.server.constant;

/**
 * auth error code enum
 *
 * @author zhangjw
 * @date 2022/12/26 0026 19:04
 */
public enum OauthErrorCode {
    /**
     * oauth error code
     */
    INVALID_REQUEST("The request is malformed, a required parameter is missing or a parameter has an invalid value"),
    INVALID_CLIENT("Client authentication failed"),
    INVALID_GRANT("Invalid authorization grant, grant invalid, grant expired, or grant revoked"),
    UNAUTHORIZED_CLIENT("Client is not authorized"),
    UNSUPPORTED_GRANT_TYPE("Authorization grant is not supported by the Authorization Server"),
    INVALID_SCOPE("The scope is malformed or invalid"),

    ACCESS_DENIED("The resource owner denied the request for authorization"),
    UNSUPPORTED_RESPONSE_TYPE("Unsupported response type"),
    SERVER_ERROR("Unexpected error"),
    TEMPORARILY_UNAVAILABLE("The authorization server is not able to handle the request"),
    ;

    private String description;

    OauthErrorCode(String description) {
        this.description = description;
    }

    public String description() {
        return description;
    }
}
