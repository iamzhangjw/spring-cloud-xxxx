package pers.zjw.xxxx.websocket.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * SimpleWebSocketConfigurer
 *
 * @author zhangjw
 * @date 2023/01/30 22:59
 */
@EnableWebSocket
@Configuration
public class SimpleWebSocketConfigurer implements WebSocketConfigurer {
    @Autowired
    private SimpleWebSocketHandler handler;
    @Autowired
    private AuthenticationInterceptor interceptor;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(handler, "/ws/**").addInterceptors(interceptor).setAllowedOrigins("*");
    }
}
