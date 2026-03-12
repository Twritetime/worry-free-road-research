package com.yanluwuyou.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("favorite")
public class Favorite extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long targetId;
    /**
     * 1: News, 2: Guide, 3: Material
     */
    private Integer targetType;
    
    // Optional: store title/cover for easier listing without joins, 
    // or just fetch by ID. Let's stick to ID for normalization, 
    // but maybe add a title field for display convenience if we don't do complex joins.
    // For simplicity in this "MVP", let's assume we might fetch details or store a snapshot.
    // Let's store basic snapshot info to avoid complex queries for now.
    private String targetTitle;
    private String targetCover;
}
