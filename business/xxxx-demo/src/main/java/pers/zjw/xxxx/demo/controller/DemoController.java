package pers.zjw.xxxx.demo.controller;

import pers.zjw.xxxx.web.controller.BaseController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * demo controller
 *
 * @author zhangjw
 * @date 2023/05/21 14:33
 */
@RequestMapping("/hello")
@RestController
public class DemoController extends BaseController {

    @GetMapping
    public String hello() {
        return "hello";
    }

    @GetMapping("/ex")
    public String helloEx() {
        throw new RuntimeException("runtime exception");
    }
}
