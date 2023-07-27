package pers.zjw.xxxx.web.exception;

import com.fasterxml.jackson.databind.util.JSONPObject;
import pers.zjw.xxxx.foundation.exception.GenericException;
import pers.zjw.xxxx.foundation.json.JsonParser;
import pers.zjw.xxxx.web.constant.ErrorCode;
import pers.zjw.xxxx.web.controller.BaseController;
import pers.zjw.xxxx.web.filter.RequestStreamWrapper;
import pers.zjw.xxxx.web.pojo.Customizable;
import pers.zjw.xxxx.web.pojo.WebResponse;
import pers.zjw.xxxx.web.context.HeaderContextHolder;
import pers.zjw.xxxx.web.context.TransferableHeaders;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.ClientAbortException;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.core.MethodParameter;
import org.springframework.core.NestedRuntimeException;
import org.springframework.core.io.Resource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ValidationException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 全局web响应及异常处理
 *
 * @date 2022/03/31 0031 14:31
 * @author zhangjw
 */
@Slf4j
@ConditionalOnClass({HttpServlet.class, ResponseBodyAdvice.class})
@RestControllerAdvice(assignableTypes = {BaseController.class})
public class WebResponseBodyAdvice implements ResponseBodyAdvice<Object> {
    private String[] paths = {"/v2/api-docs", "/swagger-resources", "/swagger-ui.html"};
    private static final AntPathMatcher matcher = new AntPathMatcher();

    @Value("${jsonp.callback:callback}")
    private String jsonpCallback = "callback";


    /**
     * 处理数据校验异常，包括：
     * 1.Validation 检验错误
     * 2.spring 对于 Servlet 的校验错误
     *
     * @param ex 异常对象
     * @return 错误返回数据结构
     */
    @ExceptionHandler({
            BindException.class, MethodArgumentNotValidException.class, ServletException.class,
            ValidationException.class, IllegalArgumentException.class})
    public WebResponse<?> handleInvalidEx(Exception ex, HttpServletRequest request) {
        String msg = null;
        List<ObjectError> errors = null;
        if (ex instanceof BindException) {
            errors = ((BindException) ex).getAllErrors();
        }
        if (ex instanceof MethodArgumentNotValidException) {
            errors = ((MethodArgumentNotValidException) ex).getBindingResult().getAllErrors();
        }
        if (!CollectionUtils.isEmpty(errors)) {
            msg = resolveMsgFromError(errors.get(0));
        }
        msg = !StringUtils.hasText(msg) ? ex.getMessage() : msg;

        return WebResponse.create(HeaderContextHolder.getInstance().getReqId(), ErrorCode.PARAM_CHECK_FAILED, msg);
    }

    /**
     * 从 ObjectError 之中获取错误信息
     */
    private String resolveMsgFromError(ObjectError objectError) {
        if (!(objectError instanceof FieldError)) {
            return objectError.getDefaultMessage();
        }
        FieldError fieldError = (FieldError) objectError;
        return fieldError.getField() + fieldError.getDefaultMessage();
    }

    /**
     * 处理 spring 运行时异常
     */
    @ExceptionHandler(NestedRuntimeException.class)
    public WebResponse<?> handleSpringRuntimeEx(NestedRuntimeException ex, HttpServletRequest request) {
        logError(ex, request);
        return WebResponse.create(HeaderContextHolder.getInstance().getReqId(), ErrorCode.BIZ_INVOKE_FAILED);
    }

    /**
     * 重复键值异常
     */
    @ExceptionHandler(DuplicateKeyException.class)
    public WebResponse<?> handleDuplicateKeyEx(DuplicateKeyException ex, HttpServletRequest request) {
        logError(ex, request);
        String msg = ErrorCode.BIZ_INVOKE_FAILED.msg();
        String errorMsg = ex.getCause().getMessage();
        if (StringUtils.hasText(errorMsg)) {
            String[] array = errorMsg.split("'");
            if (array.length >= 4) {
                String paramValue = array[1];
                String paramName = array[3];
                msg = "parameter[" + paramName + "]'s value '" + paramValue + "' exists";
            }
        }
        return WebResponse.businessInvokeFail(HeaderContextHolder.getInstance().getReqId(), msg);
    }

    /**
     * 处理客户端主动关闭连接的异常
     */
    @ExceptionHandler({ClientAbortException.class})
    public WebResponse<?> handleClientClosedEx(ClientAbortException e, HttpServletRequest request) {
        log.info("client close connection, url {}, ip {}, msg {}",
                request.getRequestURI(), request.getRemoteUser(), e.getMessage());
        return WebResponse.businessInvokeFail(HeaderContextHolder.getInstance().getReqId(), "client close request");
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(value = NoHandlerFoundException.class)
    public WebResponse<?> handleNoHandlerFoundEx(HttpServletRequest request, NoHandlerFoundException e) {
        return WebResponse.create(HeaderContextHolder.getInstance().getReqId(), ErrorCode.ILLEGAL_REQUEST, e.getMessage());
    }

    /**
     * 处理自定义 code 和 msg 的异常
     */
    @ExceptionHandler({BizException.class})
    public WebResponse<?> handleBizEx(BizException e, HttpServletRequest request) {
        logError(e, e.getMessage(), request);
        return WebResponse.create(HeaderContextHolder.getInstance().getReqId(), e.getCode(), e.getMessage());
    }

    @ExceptionHandler({GenericException.class})
    public WebResponse<?> handleGenericEx(GenericException e, HttpServletRequest request) {
        logError(e, request);
        return WebResponse.create(HeaderContextHolder.getInstance().getReqId(), ErrorCode.CODE_INTERNAL_EXCEPTION.code(), e.getMessage());
    }

    @ExceptionHandler({Exception.class})
    public WebResponse<?> handleEx(Exception e, HttpServletRequest request) {
        logError(e, request);
        return WebResponse.create(HeaderContextHolder.getInstance().getReqId(), ErrorCode.BIZ_INVOKE_FAILED, e.getMessage());
    }

    @ExceptionHandler({Throwable.class})
    public WebResponse<?> handle(Exception e, HttpServletRequest request) {
        logError(e, request);
        return WebResponse.create(HeaderContextHolder.getInstance().getReqId(), ErrorCode.REQUEST_FAILED, e.getMessage());
    }

    private void logError(Throwable throwable, HttpServletRequest request) {
        logError(throwable, null, request);
    }

    private void logError(Throwable throwable, String fullMsg, HttpServletRequest request) {
        try {
            log.error("access uri {} occurred exception: {} {}, request id {}, param: {}, body: {}",
                    HeaderContextHolder.getInstance().getUri(), throwable.getClass().getName(),
                    StringUtils.hasText(fullMsg) ? fullMsg : throwable.getMessage(),
                    HeaderContextHolder.getInstance().getReqId(),
                    JsonParser.toString(request.getParameterMap()),
                    request.getReader().lines().collect(Collectors.joining("")),
                    throwable);
        } catch (IOException e) {
            log.error("access uri controller's body occurred exception, request id {}, request mapping {}, param: {}",
                    request.getHeader("id"), request.getRequestURI(),
                    JsonParser.toString(request.getParameterMap()), e);
        }
    }

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(
            Object body, @NotNull MethodParameter returnType, @NotNull MediaType mediaType,
            @NotNull Class<? extends HttpMessageConverter<?>> converterType,
            @NotNull ServerHttpRequest httpRequest, @NotNull ServerHttpResponse response) {
        HttpServletRequest request = ((ServletServerHttpRequest) httpRequest).getServletRequest();
        TransferableHeaders headers = HeaderContextHolder.getInstance().getContext();
        String uri = headers.uri();
        if (ignoreWrapper(uri)) return body;

        if (body instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) body;
            Object status = map.get("status");
            if (Objects.nonNull(status) && isError(HttpStatus.valueOf((Integer) status))) {
                String path = (String)map.get("path");
                String error = (String)map.get("error");
                String message = (String)map.get("message");
                String msg = "call '" + path + "' but " + error + ", cause: " + message;
                body = WebResponse.fail(headers.requestId(), msg);
                response.getHeaders().add("error", "error");
                response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            }
        }

        if (!(body instanceof Customizable)) {
            if (body instanceof WebResponse) {
                WebResponse<?> webResponse = (WebResponse<?>) body;
                if (!webResponse.isSuccess()) {
                    response.getHeaders().add("error", "error");
                    response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                }
            } else if (!(body instanceof Resource) && !(body instanceof Stream)
                    && !(body instanceof ResponseEntity) && !(body instanceof ModelAndView)
                    && !(body instanceof View)) {
                body = WebResponse.ofSuccess(headers.requestId(), body);
            }

            // jsonp 支持
            String callBackFunction = request.getParameter(jsonpCallback);
            if (StringHttpMessageConverter.class.equals(converterType)) {
                // string 转换器修改 content-type
                response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                body = JsonParser.toString(body);
                // 字符串类型要自己拼接
                if (StringUtils.hasText(callBackFunction)) {
                    body = callBackFunction + "(" + body + ")";
                }
            } else if (StringUtils.hasText(callBackFunction)) {
                body = new JSONPObject(callBackFunction, body);
            }
        }

        if ("/heartbeat".equals(request.getServletPath())) {
            return body;
        }

        long cost = computeCost(headers.startTime());
        log.info("access uri {}, request id: {}, params {} method {} from {}, request content-type: {}, request body: {}, response content-type: {}, response body: {}, cost {}ms.",
                uri, headers.requestId(), JsonParser.toString(request.getParameterMap()),
                httpRequest.getMethod().name(), httpRequest.getRemoteAddress(), request.getContentType(), stringifyRequestBody(request),
                mediaType, stringifyResponseBody(mediaType, body), cost);
        return body;
    }

    private boolean isError(HttpStatus status) {
        return (status.is4xxClientError() || status.is5xxServerError());
    }

    private long computeCost(String start) {
        if (null == start) return 0;
        return System.currentTimeMillis() - Long.parseLong(start);
    }

    private String stringifyRequestBody(HttpServletRequest request) {
        if (!(request instanceof RequestStreamWrapper)) return null;
        if (MediaType.MULTIPART_FORM_DATA_VALUE.equalsIgnoreCase(request.getContentType())) {
            return null;
        }
        StringBuilder requestBody = new StringBuilder();
        try {
            request.getReader().lines().forEach(requestBody::append);
        } catch (Exception e) {
            log.error("get request occurred exception: {} {}", e.getClass().getName(), e.getMessage());
        }
        return requestBody.toString();
    }

    private String stringifyResponseBody(MediaType mediaType, Object body) {
        if (!MediaType.APPLICATION_JSON_VALUE.equalsIgnoreCase(mediaType.toString())
                && !MediaType.TEXT_PLAIN_VALUE.equalsIgnoreCase(mediaType.toString())) {
            return null;
        }
        if (body instanceof Stream || body instanceof Resource) return null;
        return (body instanceof String) ? (String) body : JsonParser.toString(body);
    }

    private boolean ignoreWrapper(String url) {
        return Stream.of(this.paths).anyMatch(path -> matcher.match(path, url));
    }
}
