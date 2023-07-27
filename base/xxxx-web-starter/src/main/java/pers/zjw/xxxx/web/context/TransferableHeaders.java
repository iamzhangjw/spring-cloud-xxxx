package pers.zjw.xxxx.web.context;

import pers.zjw.xxxx.web.constant.ConstLiteral;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.UUID;

/**
 * custom transferable headers
 *
 * @author zhangjw
 * @date 2022/12/29 0029 15:13
 */
public class TransferableHeaders extends HashMap<String, String> {
    private static final long serialVersionUID = -6203475931796255381L;

    private TransferableHeaders() {
        super();
        super.put(ConstLiteral.Header.REQ_ID, UUID.randomUUID().toString());
        super.put(ConstLiteral.Header.START, String.valueOf(System.currentTimeMillis()));
    }

    public static TransferableHeaders create(
            String requestId, String uri, String start, String authorization,
            String clientToken, String clientPayload) {
        TransferableHeaders headers = create(requestId, uri, start, authorization);
        headers.put(ConstLiteral.Header.X_CLIENT_TOKEN, clientToken);
        headers.put(ConstLiteral.Header.X_CLIENT_PAYLOAD, clientPayload);
        return headers;
    }

    public static TransferableHeaders create(String requestId, String uri, String start, String authorization) {
        TransferableHeaders headers = create(uri, authorization);
        headers.put(ConstLiteral.Header.REQ_ID, requestId);
        headers.put(ConstLiteral.Header.START, start);
        return headers;
    }

    public static TransferableHeaders create(String uri, String authorization) {
        TransferableHeaders headers = create(uri);
        if (StringUtils.hasText(authorization)) {
            headers.put(ConstLiteral.Header.AUTHORIZATION, authorization);
        }
        return headers;
    }

    public static TransferableHeaders create(String uri) {
        TransferableHeaders headers = new TransferableHeaders();
        headers.put(ConstLiteral.Header.URI, uri);
        return headers;
    }

    public String requestId() {
        return super.get(ConstLiteral.Header.REQ_ID);
    }

    public String startTime() {
        return super.get(ConstLiteral.Header.START);
    }

    public String uri() {
        return super.get(ConstLiteral.Header.URI);
    }

    public String authorization() {
        return super.get(ConstLiteral.Header.AUTHORIZATION);
    }

    public String clientToken() {
        return super.get(ConstLiteral.Header.X_CLIENT_TOKEN);
    }

    public String clientPayload() {
        return super.get(ConstLiteral.Header.X_CLIENT_PAYLOAD);
    }
}
