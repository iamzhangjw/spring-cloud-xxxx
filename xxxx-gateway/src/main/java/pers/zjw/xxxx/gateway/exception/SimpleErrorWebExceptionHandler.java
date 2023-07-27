package pers.zjw.xxxx.gateway.exception;

import pers.zjw.xxxx.foundation.json.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.DefaultErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
public class SimpleErrorWebExceptionHandler extends DefaultErrorWebExceptionHandler {

    @Autowired
    private GatewayExceptionHandler gatewayExceptionHandler;

    /**
     * Create a new {@code DefaultErrorWebExceptionHandler} instance.
     *
     * @param errorAttributes    the error attributes
     * @param resourceProperties the resource configuration properties
     * @param errorProperties    the error configuration properties
     * @param applicationContext the current application context
     */
    public SimpleErrorWebExceptionHandler(ErrorAttributes errorAttributes, ResourceProperties resourceProperties,
                                          ErrorProperties errorProperties, ApplicationContext applicationContext) {
        super(errorAttributes, resourceProperties, errorProperties, applicationContext);
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    @Override
    protected Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
        Map<String, Object> error = getErrorAttributes(request, getErrorAttributeOptions(request, MediaType.ALL));
        int errorStatus = getHttpStatus(error);
        Throwable throwable = getError(request);
        return gatewayExceptionHandler.handle(throwable)
                .doOnNext((resp) -> logError(request, errorStatus, throwable));
    }

    private void logError(ServerRequest request, int errorStatus, Throwable throwable) {
        log.error("call controller occurred {}, msg: {}, request mapping {}, param: {}",
                throwable.getClass().getName(), throwable.getMessage(),
                request.path(), JsonParser.toString(request.queryParams()), throwable);
    }
}
