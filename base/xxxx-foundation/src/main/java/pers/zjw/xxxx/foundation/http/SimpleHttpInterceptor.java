package pers.zjw.xxxx.foundation.http;

import lombok.extern.slf4j.Slf4j;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okio.Buffer;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;

/**
 * Logging request parameters and other useful information
 *
 * we also can do:
 * Adding authentication and authorization headers to our requests
 * Formatting our request and response bodies
 * Compressing the response data sent to the client
 * Altering our response headers by adding some cookies or extra header information
 *
 * @author zhangjw
 * @date 2022/12/7 0007 14:14
 */
@Slf4j
public class SimpleHttpInterceptor implements Interceptor {

    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        long startTime = System.currentTimeMillis();
        Request request = chain.request();
        Response response = chain.proceed(request);
        String reqBody = null, respBody = null;
        if (Objects.nonNull(request.body()) && printBody(request.body().contentType())) {
            reqBody = stringifyRequestBody(request);
        }
        int code = response.code();
        if (code >= 300) {
            respBody = "fail[" + response.code() + "]";
        } else if (Objects.nonNull(response.body()) && printBody(response.body().contentType())) {
            respBody = stringifyResponseBody(response);
        }
        log.debug("request header:{}", request.headers());
        log.debug("response header:{}", response.headers());
        log.info("call HTTP request {} {} with body: {}, and response body: {}, cost {}ms.",
                request.url(), request.method(), reqBody, respBody,
                (System.currentTimeMillis() - startTime));
        return response;
    }

    private boolean printBody(okhttp3.MediaType mediaType) {
        return Objects.nonNull(mediaType)
                && HttpActuator.ALLOW_PRINT_CONTENT_TYPE.contains(mediaType.toString());
    }

    private String stringifyRequestBody(Request request) {
        if (request.body().isOneShot()) return null;
        try {
            final Request copy = request.newBuilder().build();
            final Buffer buffer = new Buffer();
            copy.body().writeTo(buffer);
            return buffer.readUtf8();
        } catch (final IOException e) {
            log.warn("read body from request failed:", e);
        }
        return null;
    }

    private String  stringifyResponseBody(Response response) {
        try {
            return response.peekBody(1024).string();
        } catch (IOException e) {
            log.warn("read body from response failed:", e);
        }
        return null;
    }
}
