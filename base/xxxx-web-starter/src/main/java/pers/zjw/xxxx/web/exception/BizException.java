package pers.zjw.xxxx.web.exception;

import pers.zjw.xxxx.web.constant.ErrorCode;
import lombok.Getter;

/**
 * 業務异常
 *
 * @date 2022/03/28 0028 11:18
 * @author zhangjw
 */
@Getter
public class BizException extends RuntimeException {
    private String reqId;
    private int code;

    public BizException(ErrorCode errorCode) {
        this(null, errorCode);
    }

    public BizException(String reqId, ErrorCode errorCode) {
        this(reqId, errorCode, errorCode.msg());
    }

    public BizException(ErrorCode errorCode, String msg) {
        this(null, errorCode, msg);
    }

    public BizException(String reqId, ErrorCode errorCode, String msg) {
        super(msg);
        this.reqId = reqId;
        this.code = errorCode.code();
    }

    public BizException(ErrorCode errorCode, String msg, Throwable cause) {
        this(null, errorCode, msg, cause);
    }

    public BizException(String reqId, ErrorCode errorCode, String msg, Throwable cause) {
        super(msg, cause);
        this.reqId = reqId;
        this.code = errorCode.code();
    }

    public BizException(ErrorCode errorCode, String message, Throwable cause,
                        boolean enableSuppression, boolean writableStackTrace) {
        this(null, errorCode, message, cause, enableSuppression, writableStackTrace);
    }

    public BizException(String reqId, ErrorCode errorCode, String message, Throwable cause,
                        boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.reqId = reqId;
        this.code = errorCode.code();
    }

    @Override
    public String getMessage() {
        return code + " cause " + super.getMessage();
    }
}
