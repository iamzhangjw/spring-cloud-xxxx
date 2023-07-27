package pers.zjw.xxxx.websocket.handler;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

/**
 * DemoHandler
 *
 * @author zhangjw
 * @date 2023/02/04 20:46
 */
@Endpoint("/ws/demo")
@Component
public class DemoHandler extends BusinessHandler {
    @Override
    public WebSocketMessage<String> assemblyMessage() {
        return new TextMessage("hello");
    }

    @Override
    public void handle(WebSocketSession session, TextMessage message) {
        send(session);
    }
}
