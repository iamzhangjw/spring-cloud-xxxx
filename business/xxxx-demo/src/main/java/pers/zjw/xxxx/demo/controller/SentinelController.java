package pers.zjw.xxxx.demo.controller;

import pers.zjw.xxxx.demo.service.SentinelService;
import pers.zjw.xxxx.web.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * sentinel controller
 *
 * @author zhangjw
 * @date 2023/05/22 0022 11:18
 */
@RequestMapping("/sentinel2")
@RestController
public class SentinelController extends BaseController {
    @Autowired
    private SentinelService sentinelService;


    @GetMapping("hello1")
    public String hello1(String name) {
        return sentinelService.hello1(name);
    }

    @GetMapping("hello2")
    public String hello2(String name) {
        return sentinelService.hello2(name);
    }
}
