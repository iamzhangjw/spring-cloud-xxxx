package pers.zjw.xxxx.web.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import pers.zjw.xxxx.web.constant.ErrorCode;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.io.Serializable;

/**
 * web 响应
 *
 * @date 2022/03/31 0031 13:59
 * @author zhangjw
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WebResponse<T> implements Serializable {
    private static final long serialVersionUID = -3110395132126676705L;
    private String reqId;
    private int code;
    private String msg;
    private T data;

    public WebResponse() {

    }

    public static String defaultResponse() {
        return "{\"code\":500,\"msg\":\"internal error\"\"success\":false}";
    }

    private WebResponse(String reqId) {
        this(reqId, ErrorCode.SUCCESS.code(), ErrorCode.SUCCESS.msg());
    }

    private WebResponse(String reqId, T data) {
        this(reqId);
        this.data = data;
    }

    private WebResponse(String reqId, int code, String msg) {
        this.reqId = reqId;
        this.code = code;
        this.msg = msg;
    }

    private WebResponse(String reqId, int code, String msg, T data) {
        this(reqId, code, msg);
        this.data = data;
    }

    public static <T> WebResponse<T> ofSuccess(String reqId, T data) {
        return new WebResponse<>(reqId, data);
    }

    public static WebResponse<?> businessInvokeFail(String reqId, String message) {
        return new WebResponse<>(reqId, ErrorCode.BIZ_INVOKE_FAILED.code(), message, null);
    }

    public static WebResponse<?> codeInvokeFail(String reqId, String message) {
        return new WebResponse<>(reqId, ErrorCode.CODE_INTERNAL_EXCEPTION.code(), message, null);
    }

    public static WebResponse<?> fail(String reqId, String message) {
        return new WebResponse<>(reqId, ErrorCode.REQUEST_FAILED.code(), message, null);
    }

    public static WebResponse<?> create(String reqId, ErrorCode code) {
        return new WebResponse<>(reqId, code.code(), code.msg());
    }

    public static <T> WebResponse<T> create(String reqId, ErrorCode code, T data) {
        return new WebResponse<>(reqId, code.code(), code.msg(), data);
    }

    public static WebResponse<?> create(String reqId, ErrorCode code, String msg) {
        return new WebResponse<>(reqId, code.code(), StringUtils.hasText(msg) ? msg : code.msg());
    }

    public static <T> WebResponse<T> create(String reqId, ErrorCode code, String msg, T data) {
        return new WebResponse<>(reqId, code.code(), StringUtils.hasText(msg) ? msg : code.msg(), data);
    }

    public static WebResponse<?> create(String reqId, int code, String msg) {
        return new WebResponse<>(reqId, code, msg);
    }

    public static <T> WebResponse<T>  create(String reqId, int code, String msg, T data) {
        return new WebResponse<>(reqId, code, msg, data);
    }

    public boolean isSuccess() {
        return ErrorCode.SUCCESS.code() == this.code;
    }
}
