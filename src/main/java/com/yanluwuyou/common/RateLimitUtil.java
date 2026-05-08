package com.yanluwuyou.common;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 接口限流工具类
 * 基于滑动窗口算法实现简单的请求频率限制
 * 防止恶意请求和接口滥用
 */
@Component
public class RateLimitUtil {

    /**
     * 存储每个key的请求计数器
     */
    private final Map<String, AtomicLong> counters = new ConcurrentHashMap<>();
    
    /**
     * 存储每个key的时间窗口起始时间
     */
    private final Map<String, Long> windows = new ConcurrentHashMap<>();

    /**
     * 时间窗口大小（毫秒），默认1秒
     */
    private static final long WINDOW_SIZE_MS = 1000;
    
    /**
     * 每个时间窗口内允许的最大请求数，默认100次/秒
     */
    private static final long MAX_REQUESTS_PER_WINDOW = 100;

    /**
     * 尝试获取请求许可（使用默认限流阈值）
     * 
     * @param key 限流标识（如用户ID、IP地址等）
     * @return true-允许请求, false-拒绝请求
     */
    public boolean tryAcquire(String key) {
        return tryAcquire(key, MAX_REQUESTS_PER_WINDOW);
    }

    /**
     * 尝试获取请求许可（使用自定义限流阈值）
     * 
     * @param key 限流标识
     * @param maxRequests 每个时间窗口允许的最大请求数
     * @return true-允许请求, false-拒绝请求
     */
    public boolean tryAcquire(String key, long maxRequests) {
        long now = System.currentTimeMillis();
        long windowStart = (now / WINDOW_SIZE_MS) * WINDOW_SIZE_MS;

        counters.computeIfAbsent(key, k -> {
            windows.put(k, windowStart);
            return new AtomicLong(0);
        });

        long currentWindow = windows.get(key);
        if (currentWindow != windowStart) {
            synchronized (counters.get(key)) {
                if (currentWindow != windowStart) {
                    counters.get(key).set(0);
                    windows.put(key, windowStart);
                }
            }
        }

        long currentCount = counters.get(key).incrementAndGet();
        return currentCount <= maxRequests;
    }

    /**
     * 移除指定key的限流器
     * 
     * @param key 限流标识
     */
    public void removeLimiter(String key) {
        counters.remove(key);
        windows.remove(key);
    }

    /**
     * 清空所有限流器数据
     */
    public void clearAll() {
        counters.clear();
        windows.clear();
    }
}
