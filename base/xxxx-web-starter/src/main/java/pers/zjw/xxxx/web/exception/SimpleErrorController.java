package pers.zjw.xxxx.web.exception;

import pers.zjw.xxxx.web.constant.ErrorCode;
import pers.zjw.xxxx.web.context.HeaderContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * w错误请求处理 endpoint
 *
 * @author zhangjw
 * @date 2022/12/23 0023 17:58
 */
@Slf4j
@ConditionalOnClass({HttpServlet.class})
@Controller
@RequestMapping({"${server.error.path:${error.path:/error}}"})
public class SimpleErrorController extends BasicErrorController {

    public SimpleErrorController(ServerProperties serverProperties) {
        super(new DefaultErrorAttributes(), serverProperties.getError());
    }

    @Override
    public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {
        HttpStatus status = getStatus(request);
        Map<String, Object> errorAttributes = getErrorAttributes(request, getErrorAttributeOptions(request, MediaType.ALL));
        String path = (String)errorAttributes.get("path");
        String error = (String)errorAttributes.get("error");
        String message = (String)errorAttributes.get("message");
        String msg = "call '" + path + "' but " + error + ", cause: " + message;

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("reqId", HeaderContextHolder.getInstance().getReqId());
        body.put("code", ErrorCode.REQUEST_FAILED.code());
        body.put("msg", msg);
        return ResponseEntity.status(status).contentType(MediaType.APPLICATION_JSON).body(body);
    }

    @Override
    public ModelAndView errorHtml(HttpServletRequest request, HttpServletResponse response) {
        //请求的状态
        HttpStatus status = getStatus(request);
        response.setStatus(getStatus(request).value());
        Map<String, Object> model = getErrorAttributes(request, getErrorAttributeOptions(request, MediaType.TEXT_HTML));
        ModelAndView modelAndView = resolveErrorView(request, response, status, model);
        //指定自定义的视图
        return (modelAndView == null ? new ModelAndView("error", model) : modelAndView);
    }
}
