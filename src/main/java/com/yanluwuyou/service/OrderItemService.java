package com.yanluwuyou.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yanluwuyou.entity.OrderItem;

import java.util.List;
import java.util.Map;

/**
 * 订单项服务接口
 * 提供订单项的增删改查功能
 */
public interface OrderItemService extends IService<OrderItem> {

    /**
     * 获取资料销售排名
     * @param limit 限制数量
     * @return 排名列表
     */
    List<Map<String, Object>> getMaterialSalesRanking(int limit);
}
