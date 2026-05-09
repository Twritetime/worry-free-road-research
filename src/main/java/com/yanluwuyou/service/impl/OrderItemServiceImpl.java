package com.yanluwuyou.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanluwuyou.entity.OrderItem;
import com.yanluwuyou.mapper.OrderItemMapper;
import com.yanluwuyou.service.OrderItemService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 订单项服务实现类
 * 实现订单项的增删改查功能
 */
@Service
public class OrderItemServiceImpl extends ServiceImpl<OrderItemMapper, OrderItem> implements OrderItemService {

    @Override
    public List<Map<String, Object>> getMaterialSalesRanking(int limit) {
        List<Map<String, Object>> items = this.baseMapper.getMaterialSalesRanking(limit);
        int rank = 1;
        for (Map<String, Object> item : items) {
            item.put("rank", rank++);
            item.put("salesCount", item.get("totalQuantity"));
        }
        return items;
    }
}
