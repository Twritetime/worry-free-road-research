package com.yanluwuyou.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yanluwuyou.entity.HomeConfig;

import java.util.List;
import java.util.Map;

/**
 * 首页配置服务接口
 */
public interface HomeConfigService extends IService<HomeConfig> {

    /**
     * 获取首页配置分组（按类型分组）
     * @return Map<String, List<HomeConfig>>
     */
    Map<String, List<HomeConfig>> getHomeConfigsGrouped();

    /**
     * 根据类型获取配置列表（仅启用状态）
     * @param type 配置类型
     * @return 配置列表
     */
    List<HomeConfig> getConfigsByType(String type);

    /**
     * 根据类型获取所有配置列表（包含禁用状态）
     * @param type 配置类型
     * @return 配置列表
     */
    List<HomeConfig> getAllConfigsByType(String type);

    /**
     * 更新配置状态
     * @param id 配置ID
     * @param status 状态值
     * @return 是否成功
     */
    boolean updateStatus(Long id, Integer status);

    /**
     * 交换两个配置的排序顺序
     * @param id 当前配置ID
     * @param swapId 目标配置ID
     * @return 是否成功
     */
    boolean swapOrder(Long id, Long swapId);
}