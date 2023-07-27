package pers.zjw.xxxx.web.exception;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.BlockExceptionHandler;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowException;
import com.alibaba.csp.sentinel.slots.system.SystemBlockException;
import pers.zjw.xxxx.foundation.json.JsonParser;
import pers.zjw.xxxx.web.constant.ErrorCode;
import pers.zjw.xxxx.web.pojo.WebResponse;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

/**
 * sentinel 限流和熔断降级异常处理器
 *
 * @author zhangjw
 * @date 2022/12/24 0024 15:47
 */
//@ConditionalOnClass({SentinelProperties.class, HttpServletRequest.class})
//@Component
public class SimpleBlockExceptionHandler implements BlockExceptionHandler {
    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, BlockException e) throws IOException {
        String msg = "block by sentinel";
        if (e instanceof FlowException) {
            msg = "call " + e.getRule().getResource() + " from " + e.getRuleLimitApp() + " block by flow rule";
        } else if (e instanceof DegradeException) {
            msg = "call " + e.getRule().getResource() + " from " + e.getRuleLimitApp() + " block by degrade rule";
        } else if (e instanceof ParamFlowException) {
            msg = "call " + e.getRule().getResource() + " from " + e.getRuleLimitApp() + " block by key param flow rule";
        } else if (e instanceof SystemBlockException) {
            msg = "call " + e.getRule().getResource() + " from " + e.getRuleLimitApp() + " block by system rule";
        } else if (e instanceof AuthorityException) {
            msg = "call " + e.getRule().getResource() + " from " + e.getRuleLimitApp() + " block by authority rule";
        }
        httpServletResponse.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        httpServletResponse.setContentType("application/json; charset=UTF-8");
        JsonParser.customize().writeValue(httpServletResponse.getWriter(),
                WebResponse.create(UUID.randomUUID().toString(), ErrorCode.BLOCKED, msg));

    }
}
