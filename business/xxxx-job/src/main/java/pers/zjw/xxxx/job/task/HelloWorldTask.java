package pers.zjw.xxxx.job.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * HelloWorkTask
 *
 * @author zhangjw
 * @date 2023/06/07 11:55
 */
@Slf4j
@Component
public class HelloWorldTask {

    /**
     * cron表达式：Seconds Minutes Hours DayofMonth Month DayofWeek [Year]
     * 每10分钟扫描一次
     */
    @Scheduled(cron = "0 0/1 * ? * ?")
    public void helloWorld() {
        log.info("hello, world.");
    }
}
