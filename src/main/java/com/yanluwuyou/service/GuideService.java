package com.yanluwuyou.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yanluwuyou.entity.Guide;

import java.util.List;

/**
 * 报考指南服务接口
 * 提供指南的增删改查及院校、专业筛选功能
 */
public interface GuideService extends IService<Guide> {
    /**
     * 获取所有不重复的报考院校列表
     */
    List<String> getDistinctInstitutions();
    
    /**
     * 获取所有不重复的报考专业列表
     */
    List<String> getDistinctMajors();
}
