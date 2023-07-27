package pers.zjw.xxxx.gateway.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.boot.web.reactive.server.ReactiveWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorResourceFactory;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.codec.multipart.MultipartHttpMessageReader;
import org.springframework.http.codec.multipart.SynchronossPartHttpMessageReader;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import reactor.netty.resources.LoopResources;

/**
 * spring reactive upload file configuration
 *
 * see
 * https://stackoverflow.com/questions/57424649/spring-webflux-set-max-files-size-for-uploading-files
 * https://github.com/eaiman-shoshi/MultipartFileUpload/
 * https://stackoverflow.com/questions/57878258/trying-to-upload-file-via-spring-cloud-gateway-and-restcontroller
 * https://medium.com/@eaimanshoshi/step-by-step-procedure-of-spring-webflux-multipart-file-upload-and-read-each-line-without-saving-it-6a12be64f6ee
 *
 * @author zhangjw
 * @date 2022/12/30 0030 9:26
 */
@Configuration
@EnableWebFlux
public class GatewayWebConfiguration implements WebFluxConfigurer {
    @Value("${spring.application.name}")
    private String appName;
    @Value("${server.port}")
    private String appPort;

    @Override
    public void configureHttpMessageCodecs(ServerCodecConfigurer configurer) {
        SynchronossPartHttpMessageReader partReader = new SynchronossPartHttpMessageReader();
        partReader.setMaxParts(1);
        partReader.setMaxDiskUsagePerPart(10L * 1024L);
        partReader.setEnableLoggingRequestDetails(true);

        MultipartHttpMessageReader multipartReader = new MultipartHttpMessageReader(partReader);
        multipartReader.setEnableLoggingRequestDetails(true);

        configurer.defaultCodecs().multipartReader(multipartReader);
    }

    @Bean
    public ReactorResourceFactory resourceFactory() {
        // not work
        ReactorResourceFactory reactorResourceFactory = new ReactorResourceFactory();
        reactorResourceFactory.setLoopResourcesSupplier(() -> LoopResources.create(appName + "-" + appPort + "-"));
        return reactorResourceFactory;
    }
}
