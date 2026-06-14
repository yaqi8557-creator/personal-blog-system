package com.blog.api.common;

import com.blog.api.exception.BusinessException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public R<String> handleBusinessException(BusinessException e) {
        return R.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        return R.error(400, "参数校验失败: " + e.getBindingResult().getFieldError().getDefaultMessage());
    }

    @ExceptionHandler(Exception.class)
    public R<String> handleException(Exception e) {
        return R.error("服务器内部错误: " + e.getMessage());
    }
}
