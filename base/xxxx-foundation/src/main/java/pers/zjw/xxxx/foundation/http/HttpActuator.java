package pers.zjw.xxxx.foundation.http;

import com.fasterxml.jackson.core.type.TypeReference;
import pers.zjw.xxxx.foundation.exception.HttpCallException;
import pers.zjw.xxxx.foundation.exception.UnsupportedClassException;
import pers.zjw.xxxx.foundation.json.JsonParser;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * http call utils
 *
 * @author zhangjw
 * @date 2022/11/27 0027 14:54
 */
@Slf4j
public final class HttpActuator {
    public static final List<String> ALLOW_PRINT_CONTENT_TYPE = Arrays.asList(
            "text/plain", "text/xml", "application/json");

    public static final HttpActuator INSTANCE = new HttpActuator();

    private ConcurrentHashMap<String, OkHttpClient> clientMap;
    private OkHttpClient defaultClient;

    private HttpActuator() {
        defaultClient = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.MINUTES)
                .writeTimeout(5, TimeUnit.MINUTES)
                .addInterceptor(new SimpleHttpInterceptor())
                .build();
        clientMap = new ConcurrentHashMap<>();
    }

    OkHttpClient client() {
        return defaultClient;
    }

    OkHttpClient client(String mediaType) {
        OkHttpClient client = clientMap.get(mediaType.toString());
        if (null != client) return client;
        synchronized (INSTANCE) {
            client = clientMap.putIfAbsent(mediaType,
                    new OkHttpClient.Builder().addInterceptor(chain -> {
                        Request originalRequest = chain.request();
                        Request requestWithUserAgent = originalRequest
                                .newBuilder()
                                .header("Content-Type", mediaType.toString())
                                .build();

                        return chain.proceed(requestWithUserAgent);
                    }).connectTimeout(15, TimeUnit.SECONDS)
                            .readTimeout(30, TimeUnit.MINUTES)
                            .writeTimeout(30, TimeUnit.MINUTES).build());
        }
        return client;
    }

    public <T> T get(String uri, TypeReference<T> typeRef) {
        return get(uri, null, null, typeRef);
    }

    public <T> T get(String uri, String mediaType, TypeReference<T> typeRef) {
        return get(uri, null, Collections.singletonMap("Content-Type", mediaType), typeRef);
    }

    public <T> T get(String uri, Map<String, Object> queryParams, TypeReference<T> typeRef) {
        return get(uri, queryParams, null, typeRef);
    }

    public <T> T get(String uri, Map<String, Object> queryParams, Map<String, String> headers, TypeReference<T> typeRef) {
        return get(uri, queryParams, headers, null, typeRef);
    }

    public <T> T get(String uri, Class<T> clazz) {
        return get(uri, null, null, clazz);
    }

    public <T> T get(String uri, String mediaType, Class<T> clazz) {
        return get(uri, null, Collections.singletonMap("Content-Type", mediaType), clazz);
    }

    public <T> T get(String uri, Map<String, Object> queryParams, Class<T> clazz) {
        return get(uri, queryParams, null, clazz);
    }

    public <T> T get(String uri, Map<String, Object> queryParams, Map<String, String> headers, Class<T> clazz) {
        return get(uri, queryParams, headers, clazz, null);
    }

    public <T> T get(String uri, Map<String, Object> queryParams, Map<String, String> headers, Class<T> clazz, TypeReference<T> typeRef) {
        Assert.hasText(uri, "url must not be null");
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(uri)).newBuilder();
        if (!CollectionUtils.isEmpty(queryParams)) {
            queryParams.forEach((k,v) -> urlBuilder.addQueryParameter(k, v.toString()));
        }
        Request.Builder requestBuilder = new Request.Builder()
                .url(urlBuilder.build().toString());
        String mediaType = null;
        if (!CollectionUtils.isEmpty(headers)) {
            mediaType = headers.get("Content-Type");
            headers.forEach((k,v) -> {
                if (!"Content-Type".equalsIgnoreCase(k)) {
                    requestBuilder.addHeader(k, v);
                }
            });
        }
        Request request = requestBuilder.build();
        OkHttpClient client = (null != mediaType) ? client(mediaType) : client();
        return parseResponse(execute(client, request), request, clazz, typeRef);
    }

    public <T> T post(String uri, TypeReference<T> typeRef) {
        return post(uri, null, typeRef);
    }

    public <T> T post(String uri, String mediaType, TypeReference<T> typeRef) {
        return post(uri, mediaType, null, typeRef);
    }

    public <T> T post(String uri, String mediaType, Object body, TypeReference<T> typeRef) {
        return post(uri, null, mediaType, body, typeRef);
    }

    public <T> T post(String uri, Map<String, Object> queryParams, Object body, TypeReference<T> typeRef) {
        return post(uri, queryParams, Collections.emptyMap(), body, typeRef);
    }

    public <T> T post(String uri, Map<String, Object> queryParams, String mediaType, Object body, TypeReference<T> typeRef) {
        return post(uri, queryParams, Collections.singletonMap("Content-Type", mediaType), body, typeRef);
    }

    public <T> T post(String uri, Map<String, Object> queryParams, Map<String, String> headers, Object body, TypeReference<T> typeRef) {
        return post(uri, queryParams, headers, body, null, typeRef);
    }

    public <T> T post(String uri, Class<T> clazz) {
        return post(uri, null, clazz);
    }

    public <T> T post(String uri, String mediaType, Class<T> clazz) {
        return post(uri, mediaType, null, clazz);
    }

    public <T> T post(String uri, String mediaType, Object body, Class<T> clazz) {
        return post(uri, null, mediaType, body, clazz);
    }

    public <T> T post(String uri, Map<String, Object> queryParams, Object body, Class<T> clazz) {
        return post(uri, queryParams, Collections.emptyMap(), body, clazz);
    }

    public <T> T post(String uri, Map<String, Object> queryParams, String mediaType, Object body, Class<T> clazz) {
        return post(uri, queryParams, Collections.singletonMap("Content-Type", mediaType), body, clazz);
    }

    public <T> T post(String uri, Map<String, Object> queryParams, Map<String, String> headers, Object body, Class<T> clazz) {
        return post(uri, queryParams, headers, body, clazz, null);
    }

    public <T> T post(String uri, Map<String, Object> queryParams, Map<String, String> headers, Object body, Class<T> clazz, TypeReference<T> typeRef) {
        return exchange(uri, "POST", queryParams, headers, body, clazz, typeRef);
    }

    public <T> T exchange(String uri, String method, Map<String, Object> queryParams, String mediaType, Object body, Class<T> clazz) {
        return exchange(uri, method, queryParams, Collections.singletonMap("Content-Type", mediaType), body, clazz);
    }

    public <T> T exchange(String uri, String method, Map<String, Object> queryParams, Map<String, String> headers, Object body, Class<T> clazz) {
        return exchange(uri, method, queryParams, headers, body, clazz, null);
    }

    public <T> T exchange(String uri, String method, Map<String, Object> queryParams, String mediaType, Object body, TypeReference<T> typeRef) {
        return exchange(uri, method, queryParams, Collections.singletonMap("Content-Type", mediaType), body, typeRef);
    }

    public <T> T exchange(String uri, String method, Map<String, Object> queryParams, Map<String, String> headers, Object body, TypeReference<T> typeRef) {
        return exchange(uri, method, queryParams, headers, body, null, typeRef);
    }

    public <T> T exchange(String uri, String method, Map<String, Object> queryParams, Map<String, String> headers, Object body, Class<T> clazz, TypeReference<T> typeRef) {
        Assert.hasText(uri, "uri must not be null");
        Assert.hasText(method, "method must not be null");
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(uri)).newBuilder();
        if (!CollectionUtils.isEmpty(queryParams)) {
            queryParams.forEach((k,v) -> urlBuilder.addQueryParameter(k, v.toString()));
        }
        Request.Builder requestBuilder = new Request.Builder()
                .url(urlBuilder.build().toString());
        String mediaType = null;
        if (!CollectionUtils.isEmpty(headers)) {
            mediaType = headers.get("Content-Type");
            headers.forEach((k,v) -> {
                if (!"Content-Type".equalsIgnoreCase(k)) {
                    requestBuilder.addHeader(k, v);
                }
            });
        }
        RequestBody requestBody = null;
        if (null != body) {
            if (null == mediaType || "multipart/form-data".equalsIgnoreCase(mediaType)) {
                if (ClassUtils.isPrimitiveOrWrapper(body.getClass()) || ClassUtils.isPrimitiveArray(body.getClass())
                        || body instanceof Collection || body instanceof String) {
                    throw new UnsupportedClassException("不支持的类：" + body.getClass());
                }
                Map<?, ?> map = (body instanceof Map) ? (Map<?, ?>) body : JsonParser.customize().convertValue(body, Map.class);
                FormBody.Builder bodyBuilder = new FormBody.Builder();
                map.forEach((k,v) -> bodyBuilder.add(k.toString(), v.toString()));
                requestBody = bodyBuilder.build();
            } else {
                String bodyString = (body instanceof String) ? (String) body : JsonParser.toString(body);
                requestBody = RequestBody.create(bodyString, MediaType.parse(mediaType));
            }
        }

        Request request = requestBuilder.method(method, requestBody).build();
        OkHttpClient client = (null != mediaType) ? client(mediaType) : client();
        return parseResponse(execute(client, request), request, clazz, typeRef);
    }

    private Response execute(OkHttpClient client, Request request) {
        Call call = client.newCall(request);
        try {
            return call.execute();
        } catch (IOException e) {
            log.error("call http url {} failed:{}", request.url(), e.getMessage());
            throw new HttpCallException("execute http request failed", request.url().toString(), request.method(), e);
        }
    }

    private <T> T parseResponse(Response response, Request request, Class<T> clazz, TypeReference<T> typeRef) {
        int code = response.code();
        if (code >= 300) {
            throw new HttpCallException("parse http request response failed", request.url().toString(), request.method());
        }
        if (null == clazz && null == typeRef) return null;
        return (null != clazz) ? JsonParser.toObject(Objects.requireNonNull(response.body()).byteStream(), clazz)
                : JsonParser.toObject(Objects.requireNonNull(response.body()).byteStream(), typeRef);
    }
}
