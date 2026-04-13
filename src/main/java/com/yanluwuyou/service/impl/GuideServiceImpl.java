package com.yanluwuyou.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yanluwuyou.entity.Guide;
import com.yanluwuyou.mapper.GuideMapper;
import com.yanluwuyou.service.GuideService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 报考指南服务实现类
 */
@Service
public class GuideServiceImpl extends ServiceImpl<GuideMapper, Guide> implements GuideService {

    private static final List<String> DEFAULT_INSTITUTIONS = Arrays.asList(
            "北京大学", "清华大学", "中国人民大学", "北京航空航天大学", "北京理工大学", "中国农业大学", "北京师范大学", "中央民族大学",
            "南开大学", "天津大学", "大连理工大学", "东北大学", "吉林大学", "哈尔滨工业大学", "复旦大学", "同济大学",
            "上海交通大学", "华东师范大学", "南京大学", "东南大学", "浙江大学", "中国科学技术大学", "厦门大学", "山东大学",
            "中国海洋大学", "武汉大学", "华中科技大学", "中南大学", "湖南大学", "中山大学", "华南理工大学", "四川大学",
            "重庆大学", "电子科技大学", "西安交通大学", "西北工业大学", "兰州大学", "国防科技大学", "东北师范大学", "华中师范大学",
            "华东理工大学", "北京邮电大学", "北京交通大学", "西南交通大学", "西南财经大学", "对外经济贸易大学", "中央财经大学", "上海财经大学",
            "南京航空航天大学", "南京理工大学", "河海大学", "中国地质大学(北京)", "中国地质大学(武汉)", "中国矿业大学", "中国政法大学", "北京外国语大学",
            "上海外国语大学", "西安电子科技大学", "北京工业大学", "华北电力大学", "苏州大学", "暨南大学", "深圳大学", "郑州大学",
            "云南大学", "福州大学", "合肥工业大学", "陕西师范大学", "宁波大学", "杭州电子科技大学", "河北工业大学", "燕山大学"
    );

    @Override
    public List<String> getDistinctInstitutions() {
        QueryWrapper<Guide> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("distinct institution").isNotNull("institution").ne("institution", "");
        LinkedHashSet<String> merged = new LinkedHashSet<>(DEFAULT_INSTITUTIONS);
        baseMapper.selectObjs(queryWrapper).stream()
                .map(Object::toString)
                .filter(item -> !item.isBlank() && !"未知院校".equals(item))
                .forEach(merged::add);
        return merged.stream().collect(Collectors.toList());
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
