package com.yanluwuyou.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yanluwuyou.entity.Guide;
import org.apache.ibatis.annotations.Mapper;

/**
 * 报考指南数据访问层接口
 * 继承MyBatis-Plus的BaseMapper，提供指南实体的数据库操作
 * 对应数据库表: yl_guide
 */
@Mapper
public interface GuideMapper extends BaseMapper<Guide> {
}
