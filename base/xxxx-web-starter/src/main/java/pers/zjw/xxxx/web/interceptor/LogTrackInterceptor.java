package pers.zjw.xxxx.web.interceptor;

import pers.zjw.xxxx.web.context.HeaderContextHolder;
import pers.zjw.xxxx.web.context.TransferableHeaders;
import org.slf4j.MDC;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 日志拦截器
 * 在输出日志中添加请求id
 *
 * @author zhangjw
 * @date 2023/01/20 0020 19:19
 */
public class LogTrackInterceptor extends HandlerInterceptorAdapter {
    private final static String LOG_ID = "logId";
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        TransferableHeaders headers = HeaderContextHolder.getInstance().getContext();
        if (null != headers) {
            MDC.put(LOG_ID, headers.requestId());
        }
        return super.preHandle(request, response, handler);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        super.afterCompletion(request, response, handler, ex);
        MDC.remove(LOG_ID);
    }
}
