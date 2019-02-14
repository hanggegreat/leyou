package cn.lollipop.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@EnableZuulProxy
@SpringBootApplication
public class LyGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(LyGatewayApplication.class, args);
    }

}

