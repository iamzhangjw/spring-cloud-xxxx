package pers.zjw.xxxx.web.controller;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * exception controller
 *
 * @author zhangjw
 * @date 2022/12/28 0028 12:16
 */
@ConditionalOnClass({HttpServlet.class})
@Controller
public class ExceptionController extends BaseController {
    /**
     * 全局异常处置
     * @param request http servlet request
     */
    @RequestMapping("/exception")
    public void exception(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        // 抛出 filter 转发过来的异常，让 ControllerAdvice 处理
        throw ((RuntimeException) request.getAttribute("filter.error"));
    }
}
