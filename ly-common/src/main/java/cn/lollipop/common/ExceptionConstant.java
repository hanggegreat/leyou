package cn.lollipop.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ExceptionConstant {
    PRICE_CANNOT_BE_NULL(400, "价格不能为空！")
    ;

    private int code;
    private String msg;
}
