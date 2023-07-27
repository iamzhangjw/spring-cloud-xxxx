package pers.zjw.xxxx.gateway.exception;

import cn.hutool.jwt.JWTException;
import pers.zjw.xxxx.web.constant.ErrorCode;
import pers.zjw.xxxx.web.exception.BizException;
import pers.zjw.xxxx.web.pojo.WebResponse;
import io.netty.channel.ConnectTimeoutException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.UUID;


/**
 * gateway exception advice
 *
 * @author zhangjw
 * @date 2022/12/23 0023 11:03
 */
@Component
public class GatewayExceptionHandler {

    Mono<ServerResponse> handle(ResponseStatusException ex) {
        return ServerResponse.status(ex.getStatus())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(
                        WebResponse.fail(UUID.randomUUID().toString(), ex.getStatus().getReasonPhrase())));
    }

    Mono<ServerResponse> handle(ConnectTimeoutException ex) {
        return ServerResponse.status(HttpStatus.GATEWAY_TIMEOUT)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(
                        WebResponse.create(UUID.randomUUID().toString(), ErrorCode.GATEWAY_CONNECT_TIME_OUT)));
    }

    Mono<ServerResponse> handle(JWTException ex) {
        return ServerResponse.status(HttpStatus.UNAUTHORIZED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(
                        WebResponse.create(UUID.randomUUID().toString(), ErrorCode.TOKEN_INVALID_OR_EXPIRED)));
    }

    Mono<ServerResponse> handle(BizException ex) {
        return ServerResponse.status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(
                        WebResponse.create(ex.getReqId(), ex.getCode(), ex.getMessage())));
    }

    public Mono<ServerResponse> handle(Throwable throwable) {
        Mono<ServerResponse> result = null;
        if (throwable instanceof ConnectTimeoutException) {
            result = handle((ConnectTimeoutException) throwable);
        } else if (throwable instanceof ResponseStatusException) {
            result = handle((ResponseStatusException) throwable);
        } else if (throwable instanceof JWTException) {
            result = handle((JWTException) throwable);
        } else if (throwable instanceof BizException) {
            result = handle((BizException) throwable);
        }

        if (null != result) return result;
        return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(
                        WebResponse.fail(UUID.randomUUID().toString(), throwable.getMessage())));
    }
}
