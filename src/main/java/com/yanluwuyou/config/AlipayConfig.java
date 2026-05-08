package com.yanluwuyou.config;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 支付宝支付配置类
 * 从配置文件读取支付宝相关参数，并创建支付宝客户端Bean
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "alipay")
public class AlipayConfig {
    /**
     * 支付宝应用ID
     */
    private String appId;
    
    /**
     * 应用私钥
     */
    private String appPrivateKey;
    
    /**
     * 支付宝公钥
     */
    private String alipayPublicKey;
    
    /**
     * 异步通知回调地址
     */
    private String notifyUrl;
    
    /**
     * 同步跳转地址
     */
    private String returnUrl;
    
    /**
     * 前端跳转地址
     */
    private String frontendReturnUrl;
    
    /**
     * 支付宝网关地址
     */
    private String gatewayUrl;
    
    /**
     * 数据格式，默认json
     */
    private String format = "json";
    
    /**
     * 字符编码，默认UTF-8
     */
    private String charset = "UTF-8";
    
    /**
     * 签名类型，默认RSA2
     */
    private String signType = "RSA2";

    /**
     * 创建支付宝客户端Bean
     * 
     * @return 支付宝客户端实例
     */
    @Bean
    public AlipayClient alipayClient() {
        return new DefaultAlipayClient(gatewayUrl, appId, appPrivateKey, format, charset, alipayPublicKey, signType);
    }
}
