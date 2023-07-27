package pers.zjw.xxxx.web.access;

import cn.hutool.crypto.digest.HmacAlgorithm;
import cn.hutool.jwt.signers.JWTSignerUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;

/**
 * 受信访问校验
 *
 * @author zhangjw
 * @date 2022/12/25 19:58
 */
@Component
public class TrustedAccessCertifier {
    @Value("${xxxx.access.sign-key:xxxx}")
    private String signKey;

    @PostConstruct
    void warmup() {
        JWTSignerUtil.createSigner(HmacAlgorithm.HmacSHA512.getValue(), signKey.getBytes(StandardCharsets.UTF_8));
    }

    public String genToken() {
        // TODO 生成网关校验通过的token
        return signKey;
    }

    public boolean verify(String token) {
        //TODO 从网关获取并校验,通过校验就可信任 x-client-token-user 中的信息
        return StringUtils.hasText(token) && signKey.equals(token);
    }
}
