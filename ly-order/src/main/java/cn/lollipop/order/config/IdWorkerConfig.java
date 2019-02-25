package cn.lollipop.order.config;

import cn.lollipop.common.util.IdWorker;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(IdWorkerProperties.class)
public class IdWorkerConfig {

    @Bean
    public IdWorker idWorker(IdWorkerProperties properties) {
        return new IdWorker(properties.getWorkerId(), properties.getDataCenterId());
    }
}