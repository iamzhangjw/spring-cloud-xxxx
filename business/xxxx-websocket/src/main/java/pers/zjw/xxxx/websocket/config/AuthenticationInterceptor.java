package pers.zjw.xxxx.websocket.config;

import pers.zjw.xxxx.web.context.ClientContextHolder;
import pers.zjw.xxxx.web.pojo.ClientClaims;
import pers.zjw.xxxx.web.util.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * AuthenticationInterceptor
 *
 * @author zhangjw
 * @date 2023/01/30 23:07
 */
@Slf4j
@Component
public class AuthenticationInterceptor implements HandshakeInterceptor {
    @Override
    public boolean beforeHandshake(ServerHttpRequest httpRequest, ServerHttpResponse httpResponse, WebSocketHandler handler, Map<String, Object> map) throws Exception {
        HttpServletRequest request = ((ServletServerHttpRequest) httpRequest).getServletRequest();
        String token = request.getParameter("token");
        if (StringUtils.isNotEmpty(token)) {
            try {
                ClientClaims claims = JwtUtils.getClaimsFromAuthentication(token);
                log.debug("web socket interceptor catch authenticated user from authentication: {} {}, uri:{}",
                        claims.getClientId(), claims.getUsername(), request.getRequestURI());
                ClientContextHolder.getInstance().setContext(claims);
                return true;
            } catch (Exception e) {
                log.warn("parse token failed, token={}", token);
            }
        }
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest httpRequest, ServerHttpResponse httpResponse, WebSocketHandler handler, Exception e) {

    }
}
