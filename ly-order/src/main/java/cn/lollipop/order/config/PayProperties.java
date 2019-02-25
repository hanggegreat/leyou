package cn.lollipop.order.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
@Data
@ConfigurationProperties("ly.pay")
public class PayProperties {

    private String appId; // 公众账号ID

    private String mchId; // 商户号

    private String key; // 生成签名的密钥

    private int connectTimeoutMs; // 连接超时时间

    private int readTimeoutMs;// 读取超时时间

    private String notifyUrl; // 通知地址
}