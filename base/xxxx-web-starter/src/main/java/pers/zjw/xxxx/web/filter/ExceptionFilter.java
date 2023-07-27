package pers.zjw.xxxx.web.filter;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 异常 filer
 *
 * filter 会拦截 servlet，按照 order 执行 doFilter 方法，
 * 为了统一异常处理，在第一个 filter 中转发到 controller，
 * 让 ControllerAdvice 处理
 *
 *
 * @date 2022/04/06 0006 14:48
 * @author zhangjw
 */
@Slf4j
public class ExceptionFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        try {
            chain.doFilter(request, response);
        } catch (RuntimeException e) {
            log.warn("request for {} failed, request id {}, cause: {}, then redirect to /exception",
                    req.getServletPath(), req.getHeader("id"), e.getMessage());
            // 异常捕获，发送到 error controller
            request.setAttribute("filter.error", e);
            // 将异常分发到 "/error/exception" 控制器
            request.getRequestDispatcher("/exception").forward(request, response);
        }
    }
}
