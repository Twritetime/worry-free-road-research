package com.yanluwuyou.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yanluwuyou.entity.Material;
import org.apache.ibatis.annotations.Mapper;

/**
 * 资料商城数据访问层接口
 * 继承MyBatis-Plus的BaseMapper，提供资料商品实体的数据库操作
 * 对应数据库表: yl_material
 */
@Mapper
public interface MaterialMapper extends BaseMapper<Material> {
}
