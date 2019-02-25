package cn.lollipop.order.utils;

import cn.lollipop.common.constants.ExceptionConstant;
import cn.lollipop.common.exception.LyException;
import cn.lollipop.order.config.WxPayConfig;
import cn.lollipop.order.constants.OrderStatusConstant;
import cn.lollipop.order.mapper.OrderMapper;
import cn.lollipop.order.mapper.OrderStatusMapper;
import cn.lollipop.order.pojo.Order;
import cn.lollipop.order.pojo.OrderStatus;
import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayConstants;
import com.github.wxpay.sdk.WXPayUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class PayHelper {

    private final WXPay wxPay;
    private final WxPayConfig config;
    private final OrderMapper orderMapper;
    private final OrderStatusMapper statusMapper;

    @Autowired
    public PayHelper(WXPay wxPay, WxPayConfig config, OrderMapper orderMapper, OrderStatusMapper statusMapper) {
        this.wxPay = wxPay;
        this.config = config;
        this.orderMapper = orderMapper;
        this.statusMapper = statusMapper;
    }

    public String createPayUrl(Long orderId,Long actualPay,String desc) {

        try {
            Map<String, String> data = new HashMap<>();
            // 商品描述
            data.put("body", desc);
            // 订单号
            data.put("out_trade_no", orderId.toString());
            //货币
            data.put("fee_type", "CNY");
            //金额，单位是分
            data.put("total_fee", actualPay.toString());
            //调用微信支付的终端IP（estore商城的IP）
            data.put("spbill_create_ip", "127.0.0.1");
            //回调地址
            data.put("notify_url", config.getNotifyUrl());
            // 交易类型为扫码支付
            data.put("trade_type", "NATIVE");

            Map<String, String> result = this.wxPay.unifiedOrder(data);
            // 判断通信标识
            isSuccess(orderId, result);

            return result.get("code_url");
        } catch (Exception e) {
            log.error("[微信支付] 创建交易订单异常", e);
            return null;
        }
    }

    /**
     * 判断通信和业务是否成功
     * @param orderId
     * @param result
     */
    public void isSuccess(Long orderId, Map<String, String> result) {
        if (WXPayConstants.FAIL.equals(result.get("return_code"))) {
            log.error("[微信支付] 创建支付链接失败，订单号：{}，失败原因：{}",orderId,result.get("return_msg"));
            throw new LyException(ExceptionConstant.WX_PAY_ORDER_FAIL);
        }
        // 判断业务标识
        if (WXPayConstants.FAIL.equals(result.get("result_code"))) {
            log.error("[微信支付] 创建支付链接失败，订单号：{}，失败原因：{}",orderId,result.get("err_code_des"));
            throw new LyException(ExceptionConstant.WX_PAY_ORDER_FAIL);
        }
    }


    /**
     * 判断签名是否有效
     * @param result
     */
    public void isValidSign(Map<String, String> result) {
        try {
            // 产生签名
            String sign1 = WXPayUtil.generateSignature(result, config.getKey(), WXPayConstants.SignType.HMACSHA256);
            String sign2 = WXPayUtil.generateSignature(result, config.getKey(), WXPayConstants.SignType.MD5);
            String sign = result.get("sign");
            // 比较签名
            if (!StringUtils.equals(sign, sign1) && !StringUtils.equals(sign, sign2)) {
                log.error("[支付回调] 支付订单参数有误，订单号：{}",result.get("out_trade_no"));
                throw new LyException(ExceptionConstant.PAY_ORDER_PARAM_ERROR);
            }
        } catch (Exception e) {
            log.error("[支付回调] 支付订单参数有误，订单号：{}",result.get("out_trade_no"));
            throw new LyException(ExceptionConstant.PAY_ORDER_PARAM_ERROR);
        }
    }

    /**
     * 主动向微信查询订单状态
     * @param orderId
     * @return
     */
    public Integer queryOrder(Long orderId) {
        try {
            Map<String, String> data = new HashMap<>();
            // 订单号
            data.put("out_trade_no", orderId.toString());
            // 查询订单状态
            Map<String, String> result = wxPay.orderQuery(data);
            // 通信和业务校验
            isSuccess(orderId,result);
            // 签名校验
            isValidSign(result);
            // 金额校验
            Long totalFee = Long.valueOf(result.get("total_fee"));
            Order order = orderMapper.selectByPrimaryKey(orderId);
            Long actualPay = order.getActualPay();
            if (!totalFee.equals(actualPay)) {
                throw new LyException(ExceptionConstant.PAY_ORDER_PARAM_ERROR);
            }
            /**
             * SUCCESS—支付成功
             *
             * REFUND—转入退款
             *
             * NOTPAY—未支付
             *
             * CLOSED—已关闭
             *
             * REVOKED—已撤销（付款码支付）
             *
             * USERPAYING--用户支付中（付款码支付）
             *
             * PAYERROR--支付失败
             */
            String tradeState = result.get("trade_state");
            // SUCCESS—支付成功
            if (StringUtils.equals("SUCCESS",tradeState)) {
                // 修改订单状态
                OrderStatus orderStatus = new OrderStatus();
                orderStatus.setOrderId(orderId);
                orderStatus.setStatus(OrderStatusConstant.UNDELIVERED.value());
                orderStatus.setPaymentTime(new Date());
                int count = statusMapper.updateByPrimaryKeySelective(orderStatus);
                if (count != 1) {
                    throw new LyException(ExceptionConstant.UPDATE_ORDER_STATUS_ERROR);
                }
                return 1;
            }
            // 正在支付或未支付
            if (StringUtils.equals("NOTPAY", tradeState) || StringUtils.equals("USERPAYING", tradeState)) {
                return 0;
            }
            // 支付失败
            return 2;
        } catch (Exception e) {
            return 0;
        }
    }
}