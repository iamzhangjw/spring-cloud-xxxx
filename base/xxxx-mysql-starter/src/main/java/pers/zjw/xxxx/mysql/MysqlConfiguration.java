package pers.zjw.xxxx.mysql;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * MysqlConfiguration
 *
 * @author zhangjw
 * @date 2023/01/16 0016 15:49
 */
@EnableAspectJAutoProxy
@Configuration
public class MysqlConfiguration implements BeanPostProcessor {
    @Autowired
    private MysqlSettingProperties properties;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof DataSourceProperties) {
            DataSourceProperties dataSourceProperties = (DataSourceProperties) bean;
            // 如果账号密码加密了，可以在这里解密

        }
        return bean;
    }
}
