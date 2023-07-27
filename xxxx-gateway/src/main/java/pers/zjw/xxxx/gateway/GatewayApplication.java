package pers.zjw.xxxx.gateway;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * gateway boot class
 *
 * more detail see <a href="https://doc.springcloud.io/spring-cloud/spring-cloud-gateway.html">spring cloud gateway</a>
 *
 * @author zhangjw
 * @date 2022/12/20 23:34
 */
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"pers.zjw.xxxx.gateway", "pers.zjw.xxxx.oauth.client"})
@SpringBootApplication(scanBasePackages = {"pers.zjw.xxxx.gateway", "pers.zjw.xxxx.oauth.client"})
public class GatewayApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(GatewayApplication.class).web(WebApplicationType.REACTIVE).run(args);
    }
}
