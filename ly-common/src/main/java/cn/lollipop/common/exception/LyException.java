package cn.lollipop.common.exception;

import cn.lollipop.common.constants.ExceptionConstant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class LyException extends RuntimeException {
    private ExceptionConstant exceptionConstant;
}
