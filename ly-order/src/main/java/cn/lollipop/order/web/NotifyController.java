package cn.lollipop.order.web;

import cn.lollipop.order.service.OrderService;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/notify")
@Slf4j
public class NotifyController {

    private final OrderService orderService;

    @Autowired
    public NotifyController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * 微信支付成功回调
     *
     * @param result
     * @return
     */
    @PostMapping(value = "pay", produces = "application/xml")
    public Map<String, String> pay(@RequestBody Map<String, String> result) {
        // 处理回调
        orderService.handleNotify(result);
        // 返回成功信息
        log.info("[支付回调] 订单支付成功，订单号：{}",result.get("out_trade_no"));
        Map<String, String> map = Maps.newHashMap();
        map.put("return_code", "SUCCESS");
        map.put("return_msg", "OK");
        return map;
    }
}
