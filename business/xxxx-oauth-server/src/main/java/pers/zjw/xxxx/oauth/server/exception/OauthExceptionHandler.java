package pers.zjw.xxxx.oauth.server.exception;

import pers.zjw.xxxx.web.controller.BaseController;
import pers.zjw.xxxx.web.pojo.Customizable;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * oauth web响应及异常处理
 *
 * @date 2022/03/31 0031 14:31
 * @author zhangjw
 */
@Slf4j
@RestControllerAdvice(assignableTypes = {BaseController.class})
public class OauthExceptionHandler {

    @ExceptionHandler({InvalidClientException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Customizable handleInvalidEx(InvalidClientException e, HttpServletRequest request) {
        return OauthResponse.of(e);
    }

    @ExceptionHandler({OauthException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Customizable handleOauthEx(OauthException e, HttpServletRequest request) {
        return OauthResponse.of(e);
    }

    @Data
    static class OauthResponse implements Customizable {
        private String error;
        private String error_description;

        static OauthResponse of(OauthException e) {
            return new OauthResponse(e.code(), e.getMessage());
        }

        private OauthResponse(String code, String msg) {
            this.error = code;
            this.error_description = msg;
        }
    }
}
