package pers.zjw.xxxx.web.controller;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServlet;

/**
 * 全局 controller
 *
 * @date 2022/04/06 0006 14:51
 * @author zhangjw
 */
@ConditionalOnClass({HttpServlet.class})
@RestController
public class HealthController extends BaseController {
    /**
     * 心跳
     */
    @RequestMapping("/heartbeat")
    public void heartbeat() {
        return;
    }
}
