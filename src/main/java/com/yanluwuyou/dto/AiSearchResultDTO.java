package com.yanluwuyou.dto;

import com.yanluwuyou.entity.Material;
import lombok.Data;

import java.util.List;

/**
 * AI智能搜索结果DTO
 * 封装AI增强的搜索结果，包含语义理解和智能推荐功能
 */
@Data
public class AiSearchResultDTO {

    /**
     * 搜索结果资料列表（按相关性排序）
     */
    private List<Material> materials;

    /**
     * AI理解的用户搜索意图（自然语言描述）
     */
    private String understoodIntent;

    /**
     * 搜索优化建议（帮助用户改进搜索关键词）
     */
    private List<String> searchSuggestions;

    /**
     * 相关分类推荐（基于搜索内容推荐的其他分类）
     */
    private List<String> relatedCategories;

    /**
     * 是否使用了语义匹配（true-语义搜索, false-关键词搜索）
     */
    private Boolean semanticMatch;
}
