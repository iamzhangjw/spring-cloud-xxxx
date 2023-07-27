package pers.zjw.xxxx.web.constant;

/**
 * 常量字符串
 *
 * @author zhangjw
 * @date 2022/12/25 19:54
 */
public interface ConstLiteral {

    interface Header {
        String X_CLIENT_PAYLOAD = "x-client-payload";
        String X_CLIENT_TOKEN = "x-client-token";

        String REQ_ID = "id";
        String START = "start";
        String URI = "uri";
        String AUTHORIZATION = "Authorization";
    }

    interface Authentication {
        long NO_ONE = -1;
    }
}
