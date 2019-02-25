package cn.lollipop.order.service;

import cn.lollipop.auth.pojo.UserInfo;
import cn.lollipop.common.constants.ExceptionConstant;
import cn.lollipop.common.exception.LyException;
import cn.lollipop.common.util.IdWorker;
import cn.lollipop.item.pojo.Sku;
import cn.lollipop.order.client.AddressClient;
import cn.lollipop.order.client.ItemClient;
import cn.lollipop.order.constants.OrderStatusConstant;
import cn.lollipop.order.dto.AddressDTO;
import cn.lollipop.common.dto.CartDTO;
import cn.lollipop.order.dto.OrderDTO;
import cn.lollipop.order.mapper.OrderDetailMapper;
import cn.lollipop.order.mapper.OrderMapper;
import cn.lollipop.order.mapper.OrderStatusMapper;
import cn.lollipop.order.pojo.Order;
import cn.lollipop.order.pojo.OrderDetail;
import cn.lollipop.order.pojo.OrderStatus;
import cn.lollipop.order.utils.PayHelper;
import cn.lollipop.order.utils.ThreadLocalUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderService {
    private final OrderMapper orderMapper;
    private final OrderDetailMapper detailMapper;
    private final OrderStatusMapper statusMapper;
    private final IdWorker idWorker;
    private final ItemClient itemClient;
    private final PayHelper payHelper;

    @Autowired
    public OrderService(OrderMapper orderMapper, OrderDetailMapper detailMapper, OrderStatusMapper statusMapper, IdWorker idWorker, ItemClient itemClient, PayHelper payHelper) {
        this.orderMapper = orderMapper;
        this.detailMapper = detailMapper;
        this.statusMapper = statusMapper;
        this.idWorker = idWorker;
        this.itemClient = itemClient;
        this.payHelper = payHelper;
    }

    @Transactional
    public Long createOrder(OrderDTO orderDTO) {
        // 1. 新增订单
        Order order = new Order();
        order.setOrderDetails(new ArrayList<>());

        // 1.1 订单编号，基本信息
        long orderId = idWorker.nextId();
        order.setOrderId(orderId);
        order.setCreateTime(new Date());
        order.setPaymentType(orderDTO.getPaymentType());

        // 1.2 用户信息
        UserInfo user = ThreadLocalUtils.get();
        order.setUserId(user.getId()); // 买家id
        order.setBuyerNick(user.getUsername()); // 买家昵称
        order.setBuyerRate(false); // 是否评价

        // 1.3 收货信息
        AddressDTO addr = AddressClient.findById(orderDTO.getAddressId());
        order.setReceiver(addr.getName());
        order.setReceiverAddress(addr.getAddress());
        order.setReceiverCity(addr.getCity());
        order.setReceiverDistrict(addr.getDistrict());
        order.setReceiverMobile(addr.getPhone());
        order.setReceiverState(addr.getState());
        order.setReceiverZip(addr.getZipCode());

        // 1.4 金额
        // 把CartDTO转换为一个map，key是sku的id，value是sku的数量
        Map<Long, Integer> map = orderDTO.getCarts().stream().collect(Collectors.toMap(CartDTO::getSkuId, CartDTO::getNum));
        // 查询全部sku信息
        List<Long> ids = orderDTO.getCarts().stream().map(CartDTO::getSkuId).collect(Collectors.toList());
        List<Sku> skuList = itemClient.querySkuListByIds(ids);
        long totalPay = 0;
        for (Sku sku : skuList) {
            totalPay += sku.getPrice() * map.get(sku.getId());

            // 封装orderDetail
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setImage(StringUtils.substringBefore(sku.getImages(), ","));
            orderDetail.setNum(map.get(sku.getId()));
            orderDetail.setOrderId(orderId);
            orderDetail.setOwnSpec(sku.getOwnSpec());
            orderDetail.setPrice(sku.getPrice());
            orderDetail.setSkuId(sku.getId());
            orderDetail.setTitle(sku.getTitle());
            order.getOrderDetails().add(orderDetail);
        }
        order.setTotalPay(totalPay);
        // 实付金额 = 总金额 + 邮费 - 优惠
        order.setActualPay(totalPay - order.getPostFee());
        if (orderMapper.insertSelective(order) != 1) {
            log.error("[创建订单] 创建订单失败，orderId: {}", orderId);
            throw new LyException(ExceptionConstant.CREATE_ORDER_ERROR);
        }
        // 2. 新增订单详情
        if (detailMapper.insertList(order.getOrderDetails()) != order.getOrderDetails().size()) {
            log.error("[创建订单] 创建订单失败，orderId: {}", orderId);
            throw new LyException(ExceptionConstant.CREATE_ORDER_ERROR);
        }

        // 3. 新增订单状态
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setCreateTime(order.getCreateTime());
        orderStatus.setOrderId(orderId);
        orderStatus.setStatus(OrderStatusConstant.UNPAID.value());
        if (statusMapper.insertSelective(orderStatus) != 1) {
            log.error("[创建订单] 创建订单失败，orderId: {}", orderId);
            throw new LyException(ExceptionConstant.CREATE_ORDER_ERROR);
        }

        // 4. 减少库存
        itemClient.decreaseStock(orderDTO.getCarts());
        return orderId;
    }

    public Order queryOrderById(Long id) {
        // 查询订单
        Order order = orderMapper.selectByPrimaryKey(id);
        if (order == null) {
            throw new LyException(ExceptionConstant.ORDER_NOT_FOUND);
        }

        // 查询订单详情
        OrderDetail detail = new OrderDetail();
        detail.setOrderId(id);
        order.setOrderDetails(detailMapper.select(detail));
        if (CollectionUtils.isEmpty(order.getOrderDetails())) {
            throw new LyException(ExceptionConstant.ORDER_DETAIL_NOT_FOUND);
        }

        // 查询订单状态信息
        OrderStatus status = new OrderStatus();
        status.setOrderId(id);
        order.setOrderStatus(statusMapper.selectOne(status));
        if (order.getOrderStatus() == null) {
            throw new LyException(ExceptionConstant.ORDER_STATUS_NOT_FOUND);
        }
        return order;
    }

    public String createPayUrl(Long id) {
        // 查询订单
        Order order = queryOrderById(id);
        // 判断订单状态
        if (!OrderStatusConstant.UNPAID.value().equals(order.getOrderStatus().getStatus())) {
            throw new LyException(ExceptionConstant.ORDER_STATUS_ERROR);
        }
        // 支付金额
        long actualPay = /*order.getActualPay()*/ 1;
        // 商品描述
        String desc = order.getOrderDetails().get(0).getTitle();
        return payHelper.createPayUrl(id, actualPay, desc);
    }

    /**
     * 微信通知回调
     *
     * @return xml格式
     */
    public void handleNotify(Map<String, String> result) {
        Long orderId = Long.valueOf(result.get("out_trade_no"));

        // 数据校验
        payHelper.isSuccess(orderId, result);

        // 签名校验
        payHelper.isValidSign(result);

        // 金额校验
        Long totalFee = Long.valueOf(result.get("total_fee"));
        Order order = orderMapper.selectByPrimaryKey(orderId);
        Long actualPay = order.getActualPay();
        if (!totalFee.equals(actualPay)) {
            throw new LyException(ExceptionConstant.PAY_ORDER_PARAM_ERROR);
        }

        // 修改订单状态
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOrderId(orderId);
        orderStatus.setStatus(OrderStatusConstant.UNDELIVERED.value());
        orderStatus.setPaymentTime(new Date());
        int count = statusMapper.updateByPrimaryKeySelective(orderStatus);
        if (count != 1) {
            throw new LyException(ExceptionConstant.UPDATE_ORDER_STATUS_ERROR);
        }
    }

    /**
     * 查询订单状态
     *
     * @param orderId
     * @return 订单状态码
     */
    public Integer queryOrderStateById(Long orderId) {
        // 查订单状态表
        OrderStatus orderStatus = statusMapper.selectByPrimaryKey(orderId);
        if (orderStatus == null) {
            throw new LyException(ExceptionConstant.ORDER_STATUS_NOT_FOUND);
        }
        Integer statusCode = orderStatus.getStatus();
        // 如果已支付，则返回1
        if (OrderStatusConstant.UNDELIVERED.value().equals(statusCode)) {
            return 1;
        }
        // 如果不是已支付状态，则不能确定当前支付状态，需要去向微信主动查询订单状态
        return payHelper.queryOrder(orderId);
    }
}
