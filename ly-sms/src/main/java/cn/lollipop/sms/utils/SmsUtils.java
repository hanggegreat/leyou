package cn.lollipop.sms.utils;

import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
import com.github.qcloudsms.httpclient.HTTPException;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;

import java.io.IOException;

@Slf4j
public class SmsUtils {
    public static SmsSingleSenderResult sendSms(String[] params, String phoneNumber, Integer appId, String appKey, Integer templateId, String smsSign) {
        SmsSingleSenderResult result = null;
        try {
            SmsSingleSender sSender = new SmsSingleSender(appId, appKey);
            result = sSender.sendWithParam("86", phoneNumber,
                    templateId, params, smsSign, "", "");  // 签名参数未提供或者为空时，会使用默认签名发送短信
            if (!"OK".equals(result.errMsg)) {
                log.error("[短信服务] 信息发送失败，phoneNumber: {}，原因: {}", phoneNumber, result.errMsg);
            }
        } catch (HTTPException | JSONException | IOException e) {
            log.error("[短信服务] 信息发送失败，phoneNumber: {}, exception: {}", phoneNumber, e);
        }
        return result;
    }
}
