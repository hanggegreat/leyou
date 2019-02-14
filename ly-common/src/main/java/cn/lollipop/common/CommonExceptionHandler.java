package cn.lollipop.common;

import cn.lollipop.common.exception.LyException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import vo.ExceptionResult;

@ControllerAdvice
public class CommonExceptionHandler {

    @ExceptionHandler(LyException.class)
    public ResponseEntity<ExceptionResult> handleException(LyException e) {
        ExceptionResult result = new ExceptionResult(e.getExceptionConstant());
        return ResponseEntity.status(result.getStatus()).body(result);
    }
}
