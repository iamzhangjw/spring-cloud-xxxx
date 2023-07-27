package pers.zjw.xxxx.foundation.exception;

/**
 * json exception
 *
 * @author zhangjw
 * @date 2022/11/30 0030 18:30
 */
public class JsonException extends GenericException {
    public JsonException() {
        super();
    }

    public JsonException(String s) {
        super(s);
    }

    public JsonException(String s, Throwable t) {
        super(s, t);
    }
}
