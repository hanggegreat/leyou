package cn.lollipop.sms;

import com.google.common.collect.Maps;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LySmsApplicationTests {
    @Autowired
    private AmqpTemplate amqpTemplate;

    @Test
    public void testSend() {
        Map<String, Object> map = Maps.newHashMap();
        map.put("phoneNumber", "13122176869");
        map.put("params", new String[]{"123456", "30"});
        amqpTemplate.convertAndSend("ly.sms.exchange", "sms.verify.code", map);
    }
}
