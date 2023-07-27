package pers.zjw.xxxx.web.interceptor;

import pers.zjw.xxxx.web.constant.ConstLiteral;
import pers.zjw.xxxx.web.context.HeaderContextHolder;
import pers.zjw.xxxx.web.context.TransferableHeaders;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * http 请求 stream 包装
 * 添加自定义header
 * stream支持读多次
 *
 * @author zhangjw
 * @date 2022/12/29 0029 9:15
 */
@Slf4j
public class AddHeaderContextInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        TransferableHeaders headers = HeaderContextHolder.getInstance().getContext();
        if (null == headers) {
            String reqId = request.getHeader(ConstLiteral.Header.REQ_ID);
            String start = request.getHeader(ConstLiteral.Header.START);
            String uri = request.getHeader(ConstLiteral.Header.URI);
            String authorization = request.getHeader(ConstLiteral.Header.AUTHORIZATION);
            String clientToken = request.getHeader(ConstLiteral.Header.X_CLIENT_TOKEN);
            String clientPayload = request.getHeader(ConstLiteral.Header.X_CLIENT_PAYLOAD);
            if (StringUtils.isNotBlank(reqId)) {
                headers = TransferableHeaders.create(reqId, uri, start, authorization);
            } else {
                headers = TransferableHeaders.create(request.getServletPath(), authorization);
            }
            if (StringUtils.isNotEmpty(clientToken)) {
                headers.put(ConstLiteral.Header.X_CLIENT_TOKEN, clientToken);
                headers.put(ConstLiteral.Header.X_CLIENT_PAYLOAD, clientPayload);
            }
            log.debug("accept request：{}, headers:{}", request.getRequestURI(), headers);
            HeaderContextHolder.getInstance().setContext(headers);
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HeaderContextHolder.getInstance().clear();
    }
}
