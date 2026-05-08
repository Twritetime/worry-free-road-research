package com.yanluwuyou.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yanluwuyou.entity.News;
import org.apache.ibatis.annotations.Mapper;

/**
 * 新闻资讯数据访问层接口
 * 继承MyBatis-Plus的BaseMapper，提供新闻实体的数据库操作
 * 对应数据库表: yl_news
 */
@Mapper
public interface NewsMapper extends BaseMapper<News> {
}
