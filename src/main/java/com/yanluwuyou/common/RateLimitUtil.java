package com.yanluwuyou.common;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class RateLimitUtil {

    private final Map<String, AtomicLong> counters = new ConcurrentHashMap<>();
    private final Map<String, Long> windows = new ConcurrentHashMap<>();

    private static final long WINDOW_SIZE_MS = 1000;
    private static final long MAX_REQUESTS_PER_WINDOW = 100;

    public boolean tryAcquire(String key) {
        return tryAcquire(key, MAX_REQUESTS_PER_WINDOW);
    }

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

    public void removeLimiter(String key) {
        counters.remove(key);
        windows.remove(key);
    }

    public void clearAll() {
        counters.clear();
        windows.clear();
    }
}
