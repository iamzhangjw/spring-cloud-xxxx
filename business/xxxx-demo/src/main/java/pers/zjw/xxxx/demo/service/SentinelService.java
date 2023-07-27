package pers.zjw.xxxx.demo.service;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import pers.zjw.xxxx.foundation.exception.GenericException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * sentinel service
 *
 * @author zhangjw
 * @date 2023/05/22 0022 19:34
 */
@Slf4j
@Service
public class SentinelService {

    @SentinelResource(value = "hello1", blockHandler = "exceptionHandler")
    public String hello1(String name) {
        return "hello, " + name;
    }

    @SentinelResource(value = "hello2", fallback = "fallbackHandler")
    public String hello2(String name) {
        throw new GenericException("Fuse break");
    }

    public String exceptionHandler(String name, BlockException ex) {
        log.error( "blockHandler：" + name, ex);
        throw new GenericException("flow limit");
    }

    public String fallbackHandler(String name) {
        return "fallback";
    }
}
