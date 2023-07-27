package pers.zjw.xxxx.gateway.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import pers.zjw.xxxx.foundation.json.JsonParser;
import pers.zjw.xxxx.oauth.client.service.PermissionService;
import pers.zjw.xxxx.web.access.PathAccessibleMatcher;
import pers.zjw.xxxx.web.access.TrustedAccessCertifier;
import pers.zjw.xxxx.web.constant.AccessScope;
import pers.zjw.xxxx.web.constant.ConstLiteral;
import pers.zjw.xxxx.web.constant.ErrorCode;
import pers.zjw.xxxx.web.context.HeaderContextHolder;
import pers.zjw.xxxx.web.context.TransferableHeaders;
import pers.zjw.xxxx.web.pojo.WebResponse;
import pers.zjw.xxxx.web.util.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;

/**
 * gateway 全局 filter
 * 处理所有的请求，验证用户token是否合法及鉴权
 *
 * @author zhangjw
 * @date 2022/12/24 0024 14:35
 */
@Slf4j
@Configuration
public class AccessGlobalFilter implements GlobalFilter {

    @Value("${xxxx.oauth2.jwt.signingKey}")
    private String signingKey;

    @Autowired
    private PermissionService permissionService;
    @Autowired
    private TrustedAccessCertifier certifier;
    @Autowired
    private PathAccessibleMatcher matcher;
    @Autowired
    private DiscoveryClient discoveryClient;

    /**
     * 1.首先网关检查token是否有效，无效直接返回401，不调用签权服务
     * 2.调用签权服务器看是否对该请求有权限，有权限进入下一个filter，没有权限返回401
     *
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String authentication = request.getHeaders().getFirst(ConstLiteral.Header.AUTHORIZATION);
        String method = request.getMethodValue();
        String path = request.getPath().value();

        TransferableHeaders headers = TransferableHeaders.create(path, authentication);
        HeaderContextHolder.getInstance().setContext(headers);
        log.debug("gateway accept request url:{}, method:{}, id:{}, headers:{}",
                path, method, headers.requestId(), request.getHeaders());

        long start = Long.parseLong(headers.startTime());

        // add headers for all routes
        ServerHttpRequest.Builder builder = request.mutate();
        headers.forEach(builder::header);

        Route route = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
        if (Objects.isNull(route)) return notFound(exchange, headers.requestId());
        log.debug("path {} match route {}, forward to {}", path, route.getId(), route.getUri());
        String serviceName = route.getUri().getHost(), uri = path;
        if (path.substring(1).startsWith(serviceName)) {
            uri = path.substring(1+serviceName.length());
        }
        AccessScope scope = matcher.match(serviceName, uri);
        if (AccessScope.PUBLIC.equals(scope)) {
            return chain.filter(exchange.mutate().request(builder.build()).build())
                    .then(Mono.fromRunnable(() -> log.info("access path {} reqId {} ignore authentication",
                            path, headers.requestId())));
        }
        if (StringUtils.isEmpty(authentication) || !JwtUtils.verifyToken(authentication, signingKey)) {
            return unauthenticated(exchange, headers.requestId());
        }

        boolean success = false;
        WebResponse<Boolean> response = null;
        if (AccessScope.AUTHENTICATED.equals(scope)) {
            log.info("access path {} reqId {} ignore authorization", path, headers.requestId());
            success = true;
        } else {
            //调用签权服务验证用户是否有权限
            response = permissionService.hasPermission(authentication, serviceName, uri, method);
            if (response.isSuccess() && response.getData()) {
                success = true;
            }
        }
        if (success) {
            String clientToken = certifier.genToken();
            String clientPayload = getClientPayload(authentication);
            builder.header(ConstLiteral.Header.X_CLIENT_TOKEN, clientToken)
                    .header(ConstLiteral.Header.X_CLIENT_PAYLOAD, clientPayload);
            headers.put(ConstLiteral.Header.X_CLIENT_TOKEN, clientToken);
            headers.put(ConstLiteral.Header.X_CLIENT_PAYLOAD, clientPayload);
            return chain.filter(exchange.mutate().request(builder.build()).build())
                    .then(Mono.fromRunnable(() -> log.info("access uri {} reqId {} cost {}ms success",
                            path, headers.requestId(), System.currentTimeMillis() - start)));
        }
        log.warn("access uri {} authentication[{}] failed or is null, reqId:{}, cost {}ms",
                uri, authentication, headers.requestId(), System.currentTimeMillis() - start);
        return unauthorized(exchange, response);
    }

    /**
     * 提取 jwt token 中的数据，转为 json
     *
     */
    private String getClientPayload(String authentication) {
        return JwtUtils.getPayloadStringFromAuthentication(authentication, signingKey);
    }

    private Mono<Void> unauthenticated(ServerWebExchange exchange, String reqId) {
        return failed(exchange, HttpStatus.UNAUTHORIZED, WebResponse.create(reqId, ErrorCode.ILLEGAL_REQUEST));
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, WebResponse<Boolean> response) {
        return failed(exchange, HttpStatus.UNAUTHORIZED, response, ErrorCode.AUTHORIZE_FAILED);
    }

    private Mono<Void> notFound(ServerWebExchange exchange, String reqId) {
        return failed(exchange, HttpStatus.NOT_FOUND, WebResponse.create(reqId, ErrorCode.ILLEGAL_REQUEST));
    }

    private Mono<Void> failed(ServerWebExchange exchange, HttpStatus status, WebResponse<?> response) {
        return failed(exchange, status, response, null);
    }

    private Mono<Void> failed(ServerWebExchange exchange, HttpStatus status,
                              WebResponse<?> response, ErrorCode customizeCode) {
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().add("Content-Type", "application/json");
        byte[] bytes;
        try {
            if (null != response && response.isSuccess() && null != customizeCode) {
                bytes = JsonParser.customize().writeValueAsBytes(
                        WebResponse.create(response.getReqId(), customizeCode));
            } else {
                bytes = JsonParser.customize().writeValueAsBytes((null == response)
                        ? WebResponse.defaultResponse() : response);
            }
        } catch (JsonProcessingException e) {
            bytes = WebResponse.defaultResponse().getBytes();
        }
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
        return exchange.getResponse().writeWith(Flux.just(buffer));
    }
}
