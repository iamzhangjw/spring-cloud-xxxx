package pers.zjw.xxxx.websocket.handler;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

/**
 * Handler
 *
 * @author zhangjw
 * @date 2023/02/03 22:42
 */
public interface Handler {
    default void handle(WebSocketSession session, TextMessage message) {
        // do nothing
    }
}
