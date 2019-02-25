package cn.lollipop.order.config;

import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WxPayConfiguration {

    private final WxPayConfig wxPayConfig;

    @Autowired
    public WxPayConfiguration(WxPayConfig wxPayConfig) {
        this.wxPayConfig = wxPayConfig;
    }

    @Bean
    public WXPay wxPay() {
        return new WXPay(wxPayConfig, WXPayConstants.SignType.HMACSHA256);
    }
}