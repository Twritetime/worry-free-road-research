package com.yanluwuyou.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yanluwuyou.entity.Favorite;
import org.apache.ibatis.annotations.Mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface FavoriteMapper extends BaseMapper<Favorite> {

    @Select("SELECT * FROM favorite WHERE user_id = #{userId} AND target_id = #{targetId} AND target_type = #{targetType}")
    Favorite selectRaw(@Param("userId") Long userId, @Param("targetId") Long targetId, @Param("targetType") Integer targetType);

    @Update("UPDATE favorite SET deleted = 0 WHERE id = #{id}")
    void restore(@Param("id") Long id);
}
