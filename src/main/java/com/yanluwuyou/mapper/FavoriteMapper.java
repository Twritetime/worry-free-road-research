package com.yanluwuyou.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yanluwuyou.entity.Favorite;
import org.apache.ibatis.annotations.Mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 收藏数据访问层接口
 * 继承MyBatis-Plus的BaseMapper，提供收藏实体的数据库操作
 * 对应数据库表: favorite
 */
@Mapper
public interface FavoriteMapper extends BaseMapper<Favorite> {

    /**
     * 根据用户ID、目标ID和目标类型查询收藏记录
     * 
     * @param userId 用户ID
     * @param targetId 目标内容ID
     * @param targetType 目标类型（1-新闻, 2-指南, 3-资料）
     * @return 收藏实体对象，如果不存在则返回null
     */
    @Select("SELECT * FROM favorite WHERE user_id = #{userId} AND target_id = #{targetId} AND target_type = #{targetType}")
    Favorite selectRaw(@Param("userId") Long userId, @Param("targetId") Long targetId, @Param("targetType") Integer targetType);

    /**
     * 恢复已逻辑删除的收藏记录
     * 
     * @param id 收藏记录ID
     */
    @Update("UPDATE favorite SET deleted = 0 WHERE id = #{id}")
    void restore(@Param("id") Long id);
}
