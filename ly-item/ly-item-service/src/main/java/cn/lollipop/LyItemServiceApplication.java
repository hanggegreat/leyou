package cn.lollipop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import tk.mybatis.spring.annotation.MapperScan;

@EnableEurekaClient
@SpringBootApplication
@MapperScan("cn.lollipop.item.mapper")
public class LyItemServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(LyItemServiceApplication.class, args);
    }
}

