package pers.zjw.xxxx.web.context;

import pers.zjw.xxxx.web.pojo.ClientClaims;

/**
 * client context
 *
 * @date 2022/03/31 0031 14:04
 * @author zhangjw
 */
public class ClientContextHolder {

    private final ThreadLocal<ClientClaims> threadLocal;

    private ClientContextHolder() {
        this.threadLocal = new ThreadLocal<>();
    }

    public static ClientContextHolder getInstance() {
        return SingletonHolder.holder;
    }

    private static class SingletonHolder {
        private static final ClientContextHolder holder = new ClientContextHolder();
    }

    public void setContext(ClientClaims claims) {
        threadLocal.set(claims);
    }

    public ClientClaims getContext() {
        return threadLocal.get();
    }


    public void clear() {
        threadLocal.remove();
    }

}
