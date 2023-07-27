package pers.zjw.xxxx.web.context;


import org.springframework.util.CollectionUtils;

/**
 * request header context holder
 *
 * @author zhangjw
 * @date 2022/12/27 0027 19:24
 */
public class HeaderContextHolder {
    private ThreadLocal<TransferableHeaders> threadLocal;

    private HeaderContextHolder() {
        this.threadLocal = new ThreadLocal<>();
    }

    public static HeaderContextHolder getInstance() {
        return HeaderContextHolder.SingletonHolder.holder;
    }

    private static class SingletonHolder {
        private static final HeaderContextHolder holder = new HeaderContextHolder();
    }

    public void setContext(TransferableHeaders headers) {
        threadLocal.set(headers);
    }

    public TransferableHeaders getContext() {
        return threadLocal.get();
    }

    public String getReqId() {
        TransferableHeaders headers = threadLocal.get();
        if (null != headers && !CollectionUtils.isEmpty(headers)) {
            return headers.requestId();
        }
        return null;
    }

    public String getStart() {
        TransferableHeaders headers = threadLocal.get();
        if (null != headers && !CollectionUtils.isEmpty(headers)) {
            return headers.startTime();
        }
        return null;
    }

    public String getUri() {
        TransferableHeaders headers = threadLocal.get();
        if (null != headers && !CollectionUtils.isEmpty(headers)) {
            return headers.uri();
        }
        return null;
    }

    public String getAuthorization() {
        TransferableHeaders headers = threadLocal.get();
        if (null != headers && !CollectionUtils.isEmpty(headers)) {
            return headers.authorization();
        }
        return null;
    }

    public void clear() {
        threadLocal.remove();
    }
}
