package pers.zjw.xxxx.job;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * job boot class
 *
 * @author zhangjw
 * @date 2023/05/20 23:34
 */
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"pers.zjw.xxxx.job", "pers.zjw.xxxx.oauth.client"})
@ServletComponentScan
@SpringBootApplication(scanBasePackages = {"pers.zjw.xxxx.job", "pers.zjw.xxxx.oauth.client"})
public class JobApplication {
    public static void main(String[] args) {
        SpringApplication.run(JobApplication.class, args);
    }
}
