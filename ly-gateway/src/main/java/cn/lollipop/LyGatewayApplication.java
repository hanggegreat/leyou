package cn.lollipop;

import cn.lollipop.gateway.config.FilterProperties;
import cn.lollipop.gateway.config.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@EnableZuulProxy
@SpringBootApplication
@EnableConfigurationProperties({JwtProperties.class, FilterProperties.class})
public class LyGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(LyGatewayApplication.class, args);
    }

}

