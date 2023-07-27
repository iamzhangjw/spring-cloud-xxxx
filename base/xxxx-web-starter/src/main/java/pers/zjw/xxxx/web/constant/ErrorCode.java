package pers.zjw.xxxx.web.constant;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 错误码
 *
 * @date 2022/03/28 0028 14:51
 * @author zhangjw
 */
public enum ErrorCode {
    /**
     * 成功
     */
    SUCCESS(200, "success"),
    REQUEST_FAILED(500, "请求失败"),
    CODE_INTERNAL_EXCEPTION(501, "代码内部异常"),
    BIZ_INVOKE_FAILED(502, "业务执行失败"),

    /**
     * 公用错误码
     *
     */
    ACCESS_DENIED(10100, "访问被拒绝"),
    AUTHORIZE_FAILED(10101, "未授权访问"),
    TOKEN_INVALID_OR_EXPIRED(10102, "token无效或过期"),
    WRONG_KEY(10103, "密钥错误"),
    TOKEN_EXPIRED(10104, "token过期"),
    UNAUTHENTICATED(10105, "未验证身份"),

    TIMESTAMP_EXPIRED(10200, "请求过期"),
    INVALID_SIGN(10201, "签名无效"),
    IP_NOT_ALLOWED_ACCESS(10202, "访问 IP 不被允许调用服务，请检查对应服务账号的白名单"),
    WRONG_CERT(10203, "证书错误或失效，请检查对应服务账号的证书"),

    PARAM_CHECK_FAILED(10300, "参数校验失败，请检查参数是否缺失或格式"),
    DATA_DUPLICATION(10301, "数据重复"),
    DATA_NOT_FOUND(10302, "数据不存在"),
    
    ILLEGAL_REQUEST(19901, "非法请求，请检查 URL 是否正确"),
    SYSTEM_BUSY(19902, "系统繁忙，请稍候再试"),
    BLOCKED(19903, "访问被限流"),

    
    
    
    // 业务相关错误码，建议均以 2 开头，不同业务递增 1000
    /**
     * 文件服务错误码 200xx
     */
    FILE_READ_STREAM_FAILED(20000, "读取流失败"),
    FILE_SIZE_MISMATCH(20001, "文件大小不匹配"),
    FILE_RESOURCE_NOT_FOUND(20002, "找不到文件资源"),
    /**
     * 网关错误码 201**
     */
    GATEWAY_ERROR(20100, "网关异常"),
    GATEWAY_CONNECT_TIME_OUT(20101, "网关超时"),
    ;


    private int code;
    private String msg;

    private final static Map<Integer,ErrorCode> CODES = Stream.of(values()).collect(
            Collectors.toMap(ErrorCode::code, e -> e));

    ErrorCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static Optional<ErrorCode> parse(int code) {
        return Optional.ofNullable(CODES.get(code));
    }

    public int code() {
        return code;
    }

    public String msg() {
        return msg;
    }
}
