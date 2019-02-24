package cn.lollipop;

import cn.lollipop.sms.config.SmsProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableConfigurationProperties(SmsProperties.class)
@SpringBootApplication
@EnableEurekaClient
public class LySmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(LySmsApplication.class, args);
    }

}
