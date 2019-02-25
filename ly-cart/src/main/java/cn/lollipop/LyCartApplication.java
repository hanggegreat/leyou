package cn.lollipop;

import cn.lollipop.cart.config.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
@EnableConfigurationProperties(JwtProperties.class)
public class LyCartApplication {
    public static void main(String[] args) {
        SpringApplication.run(LyCartApplication.class, args);
    }
}
