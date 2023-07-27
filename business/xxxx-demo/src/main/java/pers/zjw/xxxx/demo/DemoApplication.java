package pers.zjw.xxxx.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * demo boot class
 *
 * @author zhangjw
 * @date 2023/05/20 23:34
 */
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"pers.zjw.xxxx.demo", "pers.zjw.xxxx.oauth.client"})
@ServletComponentScan
@SpringBootApplication(scanBasePackages = {"pers.zjw.xxxx.demo", "pers.zjw.xxxx.redis", "pers.zjw.xxxx.oauth.client"})
public class DemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}
