package pers.zjw.xxxx.foundation.exception;

/**
 * 通用 exception
 *
 * @author zhangjw
 * @date 2022/11/30 0030 14:51
 */
public class GenericException extends RuntimeException {
    private final String packagePath = "pers.zjw";

    private Throwable cause;

    public GenericException() {
        super();
    }

    public GenericException(String s) {
        super(s);
    }

    public GenericException(String s, Throwable t) {
        super(s, t);
        this.cause = t;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + extractCauseMsg();
    }

    private String extractCauseMsg() {
        StackTraceElement[] elements = getStackTrace();
        if (null != cause) {
            elements = cause.getStackTrace();
        }
        if (null == elements || elements.length == 0) return "";
        for (StackTraceElement element : elements) {
            if (element.getClassName().startsWith(packagePath)) {
                return extractTraceMsg(element);
            }
        }
        return extractTraceMsg(elements[0]);
    }

    private String extractTraceMsg(StackTraceElement element) {
        return " at " + element.getClassName() + "." + element.getMethodName() + ":" + element.getLineNumber();
    }
}
