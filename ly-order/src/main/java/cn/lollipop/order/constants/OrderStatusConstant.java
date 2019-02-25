package cn.lollipop.order.constants;


/**
 * 初始阶段：1、未付款、未发货；初始化所有数据
 * 付款阶段：2、已付款、未发货；更改付款时间
 * 发货阶段：3、已发货，未确认；更改发货时间、物流名称、物流单号
 * 成功阶段：4、已确认，未评价；更改交易结束时间
 * 关闭阶段：5、关闭； 更改更新时间，交易关闭时间。
 * 评价阶段：6、已评价
 */
public enum OrderStatusConstant {
    UNPAID(1,"未付款、未发货"),
    UNDELIVERED(2,"已付款、未发货"),
    UNCONFIRMED(3,"已发货，未确认"),
    UNCOMMENTED(4,"已确认，未评价"),
    CLOSED(5,"关闭"),
    COMMENTED(1,"已评价")
    ;
    private Integer code;
    private String desc;

    OrderStatusConstant(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer value() {
        return this.code;
    }
}