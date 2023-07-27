package pers.zjw.xxxx.oauth.server.constant;

import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * grant type enum
 *
 * @author zhangjw
 * @date 2022/12/26 0026 14:53
 */
public enum GrantType {
    /**
     * 授权码模式
     *
     * （A）用户访问客户端，后者将前者导向认证服务器。
     * （B）用户选择是否给予客户端授权。
     * （C）假设用户给予授权，认证服务器将用户导向客户端事先指定的"重定向URI"（redirection URI），同时附上一个授权码。
     * （D）客户端收到授权码，附上早先的"重定向URI"，向认证服务器申请令牌。这一步是在客户端的后台的服务器上完成的，对用户不可见。
     * （E）认证服务器核对了授权码和重定向URI，确认无误后，向客户端发送访问令牌（access token）和更新令牌（refresh token）。
     */
    AUTHORIZATION_CODE("authorization_code"){
        @Override
        public boolean support() {
            return true;
        }
    },
    /**
     * 密码模式
     *
     * （A）用户向客户端提供用户名和密码。
     * （B）客户端将用户名和密码发给认证服务器，向后者请求令牌。
     * （C）认证服务器确认无误后，向客户端提供访问令牌。
     */
    PASSWORD("password"){
        @Override
        public boolean support() {
            return true;
        }
    },
    /**
     * 刷新 access_token
     */
    REFRESH_TOKEN("refresh_token"){
        @Override
        public boolean support() {
            return true;
        }
    },
    /**
     * 简化模式
     *
     * （A）客户端将用户导向认证服务器。
     * （B）用户决定是否给于客户端授权。
     * （C）假设用户给予授权，认证服务器将用户导向客户端指定的"重定向URI"，并在URI的Hash部分包含了访问令牌。
     * （D）浏览器向资源服务器发出请求，其中不包括上一步收到的Hash值。
     * （E）资源服务器返回一个网页，其中包含的代码可以获取Hash值中的令牌。
     * （F）浏览器执行上一步获得的脚本，提取出令牌。
     * （G）浏览器将令牌发给客户端。
     */
    IMPLICIT("implicit"){
        @Override
        public boolean support() {
            return false;
        }
    },
    /**
     * 客户端模式
     *
     * （A）客户端向认证服务器进行身份认证，并要求一个访问令牌。
     * （B）认证服务器确认无误后，向客户端提供访问令牌。
     */
    CLIENT_CREDENTIALS("client_credentials"){
        @Override
        public boolean support() {
            return true;
        }
    },
    ;

    private String value;

    GrantType(String value) {
        this.value = value;
    }

    public final static Map<String, GrantType> all = Stream.of(values()).collect(
            Collectors.toMap(e -> e.name().toLowerCase(), e -> e));

    public String value() {
        return value;
    }

    public static Optional<GrantType> parse(String key) {
        if (StringUtils.hasText(key)) return Optional.ofNullable(all.get(key.toLowerCase()));
        return Optional.empty();
    }

    public abstract boolean support();
}
