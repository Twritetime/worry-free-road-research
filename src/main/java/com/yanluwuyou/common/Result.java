package com.yanluwuyou.common;

import lombok.Data;

/**
 * 统一响应结果封装类
 * 用于封装所有API接口的返回数据，保持前后端交互格式一致
 * 
 * @param <T> 数据泛型，表示返回的具体数据类型
 */
@Data
public class Result<T> {
    
    /**
     * 响应状态码: 200-成功, 其他值-失败
     */
    private Integer code; 
    
    /**
     * 响应消息描述
     */
    private String message;
    
    /**
     * 响应数据载荷
     */
    private T data;

    /**
     * 返回成功结果（无数据）
     * 
     * @param <T> 数据类型
     * @return 成功的Result对象（data为null）
     */
    public static <T> Result<T> success() {
        return success(null);
    }

    /**
     * 返回成功结果（带数据）
     * 
     * @param data 响应数据
     * @param <T> 数据类型
     * @return 成功的Result对象
     */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("Success");
        result.setData(data);
        return result;
    }

    /**
     * 返回错误结果（指定状态码和消息）
     * 
     * @param code 错误状态码
     * @param message 错误消息
     * @param <T> 数据类型
     * @return 失败的Result对象
     */
    public static <T> Result<T> error(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

    /**
     * 返回错误结果（默认500状态码）
     * 
     * @param message 错误消息
     * @param <T> 数据类型
     * @return 失败的Result对象（code=500）
     */
    public static <T> Result<T> error(String message) {
        return error(500, message);
    }
}
