package pers.zjw.xxxx.websocket.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * demo controller
 *
 * @author zhangjw
 * @date 2022/12/23 0023 20:48
 */
@RequestMapping
@RestController
public class DemoController {

    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }
}
