package pers.zjw.xxxx.websocket.config;

import pers.zjw.xxxx.web.context.ClientContextHolder;
import pers.zjw.xxxx.web.pojo.ClientClaims;
import pers.zjw.xxxx.websocket.handler.HandlerDispatcher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

/**
 * SimpleWebSocketHandler
 *
 * @author zhangjw
 * @date 2023/01/30 23:02
 */
@Slf4j
@Component
public class SimpleWebSocketHandler extends AbstractWebSocketHandler {
    @Autowired
    private HandlerDispatcher dispatcher;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.debug("accept connected from {} , id {}", session.getUri(), session.getId());
        ClientClaims claims = ClientContextHolder.getInstance().getContext();
        if (null == claims) {
            log.debug("unauthenticated web socket {} from {}, id ={}, refuse it.",
                    session.getUri(), session.getRemoteAddress(), session.getId());
            session.close();
            return;
        }
        dispatcher.connected(claims, session);
        super.afterConnectionEstablished(session);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        if (message instanceof TextMessage) {
            dispatcher.handle(session, (TextMessage)message);
        } else if (message instanceof BinaryMessage) {
            // do something
        } else if (message instanceof PongMessage) {
            // do something
        }
        super.handleMessage(session, message);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        ClientClaims claims = ClientContextHolder.getInstance().getContext();
        if (null != claims) {
            dispatcher.disconnect(claims, session);
            ClientContextHolder.getInstance().clear();
        }
        super.afterConnectionClosed(session, status);
    }
}
