package com.yanluwuyou.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

/**
 * 全局异常处理器
 * 统一捕获和处理系统中抛出的各类异常，返回标准化的错误响应
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 处理文件上传大小超出限制的异常
     * 
     * @param e 文件上传大小异常
     * @return 错误响应结果（状态码413）
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public Result<?> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        log.warn("Upload file is too large: {}", e.getMessage());
        return Result.error(413, "上传文件过大，请上传不超过 50MB 的文件");
    }

    /**
     * 处理安全相关异常
     * 
     * @param e 安全异常
     * @return 错误响应结果（使用异常自带的状态码）
     */
    @ExceptionHandler(SecurityException.class)
    public Result<?> handleSecurityException(SecurityException e) {
        log.warn("Security exception: {}", e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理非法参数异常
     * 
     * @param e 非法参数异常
     * @return 错误响应结果（状态码400）
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public Result<?> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("Illegal argument: {}", e.getMessage());
        return Result.error(400, e.getMessage());
    }

    /**
     * 处理所有未被其他处理器捕获的通用异常
     * 
     * @param e 通用异常
     * @return 错误响应结果（状态码500）
     */
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {
        log.error("System Error", e);
        return Result.error(e.getMessage());
    }
}
