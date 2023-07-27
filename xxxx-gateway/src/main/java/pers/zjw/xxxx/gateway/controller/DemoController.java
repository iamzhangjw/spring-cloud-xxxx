package pers.zjw.xxxx.gateway.controller;

import pers.zjw.xxxx.gateway.provider.DemoProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * demo controller
 *
 * @author zhangjw
 * @date 2022/12/23 0023 20:48
 */
@RequestMapping
@RestController
public class DemoController {
    @Autowired
    private DemoProvider provider;

    @GetMapping("/hello")
    public Mono<String> hello() {
        return Mono.just("hello");
    }

    @GetMapping("/hello2")
    public Mono<String> hello2() {
        throw new RuntimeException("exception");
    }

    @GetMapping("/hello/demo")
    public Mono<String> hello3() {
        return Mono.just(provider.hello());
    }
}
