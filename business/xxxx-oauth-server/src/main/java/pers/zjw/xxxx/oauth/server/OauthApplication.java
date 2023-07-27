package pers.zjw.xxxx.oauth.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * oauth boot class
 *
 *
 * @author zhangjw
 * @date 2022/12/25 12:05
 */
@EnableDiscoveryClient
@EnableFeignClients
@ServletComponentScan
@SpringBootApplication(scanBasePackages = {
        "pers.zjw.xxxx.oauth.server", "pers.zjw.xxxx.mysql",
        "pers.zjw.xxxx.redis", "pers.zjw.xxxx.web"})
public class OauthApplication {
    public static void main(String[] args) {
        SpringApplication.run(OauthApplication.class, args);
    }
}
