package pers.zjw.xxxx.web.interceptor;

import pers.zjw.xxxx.web.context.HeaderContextHolder;
import pers.zjw.xxxx.web.context.TransferableHeaders;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;

/**
 * 拦截 feign 请求，添加 header用于鉴权
 *
 * @author zhangjw
 * @date 2022/12/29 0029 14:53
 */
@Component
public class FeignRequestInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        TransferableHeaders headers = HeaderContextHolder.getInstance().getContext();
        headers.forEach(template::header);
    }
}
