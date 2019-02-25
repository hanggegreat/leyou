package cn.lollipop.order.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "ly.worker")
public class IdWorkerProperties {

    private Long workerId;
    private Long dataCenterId;
}