package pers.zjw.xxxx.websocket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * web socket boot class
 *
 * @author zhangjw
 * @date 2022/12/20 23:34
 */
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"pers.zjw.xxxx.websocket", "pers.zjw.xxxx.oauth.client"})
@ServletComponentScan
@SpringBootApplication(scanBasePackages = {"pers.zjw.xxxx.websocket", "pers.zjw.xxxx.oauth.client"})
public class WebSocketApplication {
    public static void main(String[] args) {
        SpringApplication.run(WebSocketApplication.class, args);
    }
}
