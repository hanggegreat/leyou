package cn.lollipop.order.web;

import cn.lollipop.order.dto.OrderDTO;
import cn.lollipop.order.pojo.Order;
import cn.lollipop.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * 创建订单
     *
     * @param orderDTO
     * @return
     */
    @PostMapping
    public ResponseEntity<Long> createOrder(@RequestBody OrderDTO orderDTO) {
        return ResponseEntity.ok(orderService.createOrder(orderDTO));
    }

    @GetMapping("{id}")
    public ResponseEntity<Order> queryOrderById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(orderService.queryOrderById(id));
    }

    /**
     * 创建支付链接
     *
     * @param id
     * @return
     */
    @GetMapping("url/{id}")
    public ResponseEntity<String> createPayUrl(@PathVariable("id") Long id) {
        return ResponseEntity.ok(orderService.createPayUrl(id));
    }

    /**
     * 查询订单状态
     *
     * @param orderId
     * @return 订单状态码
     */
    @GetMapping("state/{orderId}")
    public ResponseEntity<Integer> queryOrderStateById(@PathVariable("orderId") Long orderId) {
        return ResponseEntity.ok(orderService.queryOrderStateById(orderId));
    }
}
