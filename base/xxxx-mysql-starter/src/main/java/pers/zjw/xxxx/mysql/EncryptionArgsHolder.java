package pers.zjw.xxxx.mysql;

import java.util.Map;

/**
 * EncryptionArgsHolder
 *
 * @author zhangjw
 * @date 2023/01/24 19:05
 */
public class EncryptionArgsHolder {
    private ThreadLocal<Map<String, String>> threadLocal;

    private EncryptionArgsHolder() {
        this.threadLocal = new ThreadLocal<>();
    }

    public static EncryptionArgsHolder getInstance() {
        return SingletonHolder.holder;
    }

    private static class SingletonHolder {
        private static final EncryptionArgsHolder holder = new EncryptionArgsHolder();
    }

    public void setContext(Map<String, String> args) {
        threadLocal.set(args);
    }

    public Map<String, String> getContext() {
        return threadLocal.get();
    }


    public void clear() {
        threadLocal.remove();
    }
}
