package com.yanluwuyou.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanluwuyou.entity.Guide;
import com.yanluwuyou.mapper.GuideMapper;
import com.yanluwuyou.service.GuideService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 报考指南服务实现类
 */
@Service
public class GuideServiceImpl extends ServiceImpl<GuideMapper, Guide> implements GuideService {

    @Override
    public List<String> getDistinctInstitutions() {
        QueryWrapper<Guide> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("distinct institution").isNotNull("institution").ne("institution", "");
        return baseMapper.selectObjs(queryWrapper).stream()
                .map(Object::toString)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getDistinctMajors() {
        QueryWrapper<Guide> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("distinct major").isNotNull("major").ne("major", "");
        return baseMapper.selectObjs(queryWrapper).stream()
                .map(Object::toString)
                .collect(Collectors.toList());
    }
}
