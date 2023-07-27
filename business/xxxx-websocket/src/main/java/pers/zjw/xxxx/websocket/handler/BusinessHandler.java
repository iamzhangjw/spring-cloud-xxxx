package pers.zjw.xxxx.websocket.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * BusinessHandler
 *
 * @author zhangjw
 * @date 2023/02/03 22:43
 */
@Slf4j
public abstract class BusinessHandler implements Handler {
    private Map<String, Collection<WebSocketSession>> sessionMap = new ConcurrentHashMap<>(1024);
    private Map<String, WebSocketSession> noUserSessionMap = new ConcurrentHashMap<>(1024);

    private Map<String, Boolean> lock = new ConcurrentHashMap<>(1024);

    protected void connected(String username, WebSocketSession session) throws InterruptedException {
        if (StringUtils.hasText(username)) {
            while (null != lock.putIfAbsent(username, true)) {
                TimeUnit.MILLISECONDS.sleep(30);
            }
            Collection<WebSocketSession> coll = sessionMap.computeIfAbsent(username, k -> new LinkedList<>());
            coll.add(session);
            lock.remove(username);
        } else {
            noUserSessionMap.put(session.getId(), session);
        }
    }

    protected void connected(WebSocketSession session) throws InterruptedException {
        connected(null, session);
    }

    protected void disconnect(String username, WebSocketSession session) throws InterruptedException {
        if (StringUtils.hasText(username)) {
            while (null != lock.putIfAbsent(username, true)) {
                TimeUnit.MILLISECONDS.sleep(30);
            }
            Collection<WebSocketSession> coll = sessionMap.get(username);
            if (!CollectionUtils.isEmpty(coll)) {
                coll.remove(session);
            }
            lock.remove(username);
        } else {
            noUserSessionMap.remove(session.getId());
        }
    }

    protected void disconnect(WebSocketSession session) throws InterruptedException {
        disconnect(null, session);
    }

    protected Collection<WebSocketSession> getSessionByUser(String username) {
        return sessionMap.get(username);
    }

    protected WebSocketSession getSession(String id) {
        if (!CollectionUtils.isEmpty(sessionMap)) {
            for (Collection<WebSocketSession> sessions : sessionMap.values()) {
                if (CollectionUtils.isEmpty(sessions)) continue;
                for (WebSocketSession session : sessions) {
                    if (id.equals(session.getId())) return session;
                }
            }
        }
        return noUserSessionMap.get(id);
    }

    public final void broadcast() {
        if (CollectionUtils.isEmpty(sessionMap)) return;
        for (Map.Entry<String, Collection<WebSocketSession>> entry : sessionMap.entrySet()) {
            if (CollectionUtils.isEmpty(entry.getValue())) continue;
            for (WebSocketSession session : entry.getValue()) {
                send(session);
            }
        }
    }

    public final void unicast(String username) {
        Collection<WebSocketSession> sessions = sessionMap.get(username);
        if (CollectionUtils.isEmpty(sessions)) return;
        for (WebSocketSession session : sessions) {
            send(session);
        }
    }

    public final void multicast(Collection<String> usernames) {
        if (CollectionUtils.isEmpty(usernames)) return;
        for (String username : usernames) {
            unicast(username);
        }
    }

    protected void send(WebSocketSession session) {
        try {
            session.sendMessage(assemblyMessage());
        } catch (IOException e) {
            log.warn("web socket {} send msg to {} failed", session.getUri(), session.getId());
        }
    }

    public abstract WebSocketMessage<String> assemblyMessage();
}
