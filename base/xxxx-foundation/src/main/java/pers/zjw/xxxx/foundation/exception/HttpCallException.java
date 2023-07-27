package pers.zjw.xxxx.foundation.exception;

/**
 * http call exception
 *
 * @author zhangjw
 * @date 2022/11/30 0030 18:35
 */
public class HttpCallException extends GenericException {
    private String url;
    private String method;
    public HttpCallException() {
        super();
    }

    @Override
    public String getMessage() {
        return super.getMessage() + " with " + url + ", " + method;
    }

    public HttpCallException(String msg, String url, String method) {
        super(msg);
        this.url = url;
        this.method = method;
    }

    public HttpCallException(String msg, String url, String method, Throwable throwable) {
        super(msg, throwable);
        this.url = url;
        this.method = method;
    }

}
