package pers.zjw.xxxx.mongodb;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.convert.*;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

/**
 * mongodb configuration
 *
 * @author zhangjw
 * @date 2022/12/20 21:59
 */
@Configuration
public class MongodbConfiguration implements BeanPostProcessor {
    @Autowired
    private MongoSettingProperties properties;

    @Bean
    public MappingMongoConverter mongoConverter(
            MongoDatabaseFactory factory, MongoMappingContext context, MongoCustomConversions conversions) {
        DbRefResolver dbRefResolver = new DefaultDbRefResolver(factory);
        MappingMongoConverter mappingConverter = new MappingMongoConverter(dbRefResolver, context);
        mappingConverter.setCustomConversions(conversions);
        //设置序列化时不要生成_class字段
        DefaultMongoTypeMapper typeMapper = new DefaultMongoTypeMapper(null);
        mappingConverter.setTypeMapper(typeMapper);
        return mappingConverter;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof MongoProperties) {
            MongoProperties mongoProperties = (MongoProperties) bean;
            // 如果账号密码加密了，可以在这里解密

        }
        return bean;
    }
}
