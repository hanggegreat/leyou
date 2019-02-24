package cn.lollipop.sms.mq;

import cn.lollipop.sms.config.SmsProperties;
import cn.lollipop.sms.utils.SmsUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Map;

@Component
@Slf4j
public class SmsListener {
    private static final String KEY_PREFIX = "sms:phone";

    private final SmsProperties smsProperties;
    private final StringRedisTemplate redisTemplate;

    @Autowired
    public SmsListener(SmsProperties smsProperties, StringRedisTemplate redisTemplate) {
        this.smsProperties = smsProperties;
        this.redisTemplate = redisTemplate;
    }

    /**
     * 发送短信验证码
     *
     * @param map 参数
     */
    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(name = "sms.verify.code.queue", durable = "true"),
                    exchange = @Exchange(name = "ly.sms.exchange", type = ExchangeTypes.TOPIC),
                    key = "sms.verify.code"
            )
    )
    public void listenSms(Map<String, Object> map) {
        if (CollectionUtils.isEmpty(map) || !map.containsKey("phoneNumber") || !map.containsKey("params")) {
            return;
        }
        String phoneNumber = (String) map.get("phoneNumber");
        String[] params = (String[]) map.get("params");
        if (StringUtils.isBlank(phoneNumber) || params.length != 2) {
            return;
        }

        String key = KEY_PREFIX + phoneNumber;
        if (StringUtils.isNotBlank(redisTemplate.opsForValue().get(key))) {
            log.info("[短信服务] 短信发送频率过高，被拦截，手机号码: {}", phoneNumber);
            return;
        }

        // 进行短信发送操作
        SmsUtils.sendSms(params, phoneNumber, smsProperties.getAppId(), smsProperties.getAppKey(), smsProperties.getTemplateId(), smsProperties.getSmsSign());
        log.info("[短信服务]：发送短信，手机号: {}", phoneNumber);
    }
}
