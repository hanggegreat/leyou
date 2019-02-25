package cn.lollipop.common.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ExceptionConstant {

    UPLOAD_FILE_ERROR(500, "文件上传失败！"),
    INVALID_FILE_TYPE(400, "无效的文件类型！"),
    INVALID_PARAM(400, "参数有误！"),
    INVALID_VERIFY_CODE(400, "验证码错误！"),
    INVALID_USERNAME_OR_PASSWORD(400, "用户名或密码错误！"),
    CATEGORY_NOT_FOUND(404, "未查询到商品分类信息！"),
    BRAND_NOT_FOUNT(404, "未查询到品牌信息！"),
    BRAND_SAVE_ERROR(500, "品牌信息增加失败！"),
    SPEC_GROUP_NOT_FOUND(404, "未查询到商品规格信息！"),
    SPEC_PARAM_NOT_FOUND(404, "未查询到商品规格参数信息！"),
    SPEC_GROUP_UPDATE_ERROR(500, "商品规格信息更新失败！"),
    SPEC_GROUP_SAVE_ERROR(500, "商品规格信息增加失败！"),
    SPEC_PARAM_SAVE_ERROR(500, "商品规格参数信息增加失败！"),
    SPEC_PARAM_UPDATE_ERROR(500, "商品规格参数信息更新失败！"),
    GOODS_NOT_FOUND(404, "商品信息不存在！"),
    GOODS_SKU_NOT_FOUND(404, "商品SKU不存在！"),
    GOODS_SAVE_ERROR(500, "商品信息保存失败！"),
    GOODS_UPDATE_ERROR(500, "商品信息更新失败！"),
    GOODS_DETAIL_NOT_FOUND(404, "商品详细信息不存在！"),
    GOODS_STOCK_NOT_FOUND(404, "商品库存信息不存在！"),
    CREATE_TOKEN_ERROR(500, "用户凭证创建失败！"),
    UNAUTHORIZED(403, "未授权用户！"),
    CART_NOT_FOUND(404, "购物车为空！"),
    CREATE_ORDER_ERROR(500, "订单创建失败！"),
    STOCK_NOT_ENOUGH(500, "库存量不足！"),
    ORDER_NOT_FOUND(404, "订单不存在！"),
    ORDER_DETAIL_NOT_FOUND(404, "订单详情不存在！"),
    ORDER_STATUS_NOT_FOUND(404, "订单状态信息不存在！"),
    WX_PAY_ORDER_FAIL(500,"微信下单失败！"),
    PAY_ORDER_PARAM_ERROR(400,"订单参数有误！"),
    ORDER_STATUS_ERROR(400,"订单状态异常！"),
    UPDATE_ORDER_STATUS_ERROR(500,"更新订单状态失败！");

    private int code;
    private String msg;
}
