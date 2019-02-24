package cn.lollipop.sms.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "ly.sms")
public class SmsProperties {
    private Integer appId;
    private String appKey;
    private Integer templateId;
    private String smsSign;
}
