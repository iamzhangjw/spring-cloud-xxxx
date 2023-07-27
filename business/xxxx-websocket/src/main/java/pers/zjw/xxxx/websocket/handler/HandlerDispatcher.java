package pers.zjw.xxxx.websocket.handler;

import pers.zjw.xxxx.web.pojo.ClientClaims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * HandlerDispatcher
 *
 * @author zhangjw
 * @date 2023/02/03 22:43
 */
@Slf4j
@Component
public class HandlerDispatcher implements Handler {
    private Map<String, BusinessHandler> handlerMap;

    @Autowired
    void setHandlers(List<BusinessHandler> handlers) {
        handlerMap = new HashMap<>();
        for (BusinessHandler handler : handlers) {
            Endpoint endpoint = handler.getClass().getAnnotation(Endpoint.class);
            if (null == endpoint) continue;
            handlerMap.put(endpoint.value(), handler);
        }
    }

    public void connected(ClientClaims claims, WebSocketSession session) throws InterruptedException {
        log.debug("web socket connect success, username={}, uri={}, session={}",
                claims.getUsername(), session.getUri(), session.getId());
        BusinessHandler handler = lookup(session);
        if (null == handler) return;
        handler.connected(claims.getUsername(), session);
    }

    public void disconnect(ClientClaims claims, WebSocketSession session) throws InterruptedException {
        log.debug("web socket disconnect, username={}, uri={}, session={}",
                claims.getUsername(), session.getUri(), session.getId());
        BusinessHandler handler = lookup(session);
        if (null == handler) return;
        handler.disconnect(claims.getUsername(), session);
    }

    @Override
    public void handle(WebSocketSession session, TextMessage message) {
        log.debug("receive msg:{} from {}", message.getPayload(), session.getId());
        BusinessHandler handler = lookup(session);
        if (null == handler) return;
        handler.handle(session, message);
    }

    private BusinessHandler lookup(WebSocketSession session) {
        return handlerMap.get(session.getUri().getPath());
    }
}
