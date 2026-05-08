package com.yanluwuyou.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanluwuyou.entity.HomeConfig;
import com.yanluwuyou.mapper.HomeConfigMapper;
import com.yanluwuyou.service.HomeConfigService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 首页配置服务实现类
 */
@Service
public class HomeConfigServiceImpl extends ServiceImpl<HomeConfigMapper, HomeConfig> implements HomeConfigService {

    private static final List<String> CONFIG_TYPES = Arrays.asList("banner", "notice", "hot", "recommend");

    @Override
    public Map<String, List<HomeConfig>> getHomeConfigsGrouped() {
        Map<String, List<HomeConfig>> result = new LinkedHashMap<>();
        
        LambdaQueryWrapper<HomeConfig> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(HomeConfig::getStatus, 1)
                   .orderByAsc(HomeConfig::getSortOrder)
                   .orderByAsc(HomeConfig::getId);
        
        List<HomeConfig> allConfigs = list(queryWrapper);
        
        for (String type : CONFIG_TYPES) {
            List<HomeConfig> configs = allConfigs.stream()
                    .filter(c -> type.equals(c.getType()))
                    .toList();
            result.put(type, configs);
        }
        
        return result;
    }

    @Override
    public List<HomeConfig> getConfigsByType(String type) {
        LambdaQueryWrapper<HomeConfig> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(HomeConfig::getType, type)
                   .eq(HomeConfig::getStatus, 1)
                   .orderByAsc(HomeConfig::getSortOrder)
                   .orderByAsc(HomeConfig::getId);
        return list(queryWrapper);
    }

    @Override
    public List<HomeConfig> getAllConfigsByType(String type) {
        LambdaQueryWrapper<HomeConfig> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(HomeConfig::getType, type)
                   .orderByDesc(HomeConfig::getStatus)
                   .orderByAsc(HomeConfig::getSortOrder)
                   .orderByAsc(HomeConfig::getId);
        return list(queryWrapper);
    }

    @Override
    public boolean updateStatus(Long id, Integer status) {
        HomeConfig config = getById(id);
        if (config == null) {
            return false;
        }
        config.setStatus(status);
        return updateById(config);
    }

    @Override
    public boolean swapOrder(Long id, Long swapId) {
        if (id.equals(swapId)) {
            return true;
        }
        
        HomeConfig current = getById(id);
        HomeConfig target = getById(swapId);
        
        if (current == null || target == null) {
            return false;
        }
        
        if (current.getSortOrder() == null || current.getSortOrder() == 0) {
            current.setSortOrder(current.getId().intValue());
        }
        if (target.getSortOrder() == null || target.getSortOrder() == 0) {
            target.setSortOrder(target.getId().intValue());
        }
        
        Integer tempSort = current.getSortOrder();
        current.setSortOrder(target.getSortOrder());
        target.setSortOrder(tempSort);
        
        return updateById(current) && updateById(target);
    }
}