package com.yanluwuyou.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yanluwuyou.entity.Guide;

import java.util.List;

/**
 * 报考指南服务接口
 */
public interface GuideService extends IService<Guide> {
    List<String> getDistinctInstitutions();
    List<String> getDistinctMajors();
}
