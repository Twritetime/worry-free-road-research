package com.yanluwuyou.dto;

import com.yanluwuyou.entity.Material;
import lombok.Data;

import java.util.List;

/**
 * AI智能搜索结果DTO
 */
@Data
public class AiSearchResultDTO {

    /**
     * 搜索结果资料列表
     */
    private List<Material> materials;

    /**
     * AI理解的搜索意图
     */
    private String understoodIntent;

    /**
     * 搜索建议
     */
    private List<String> searchSuggestions;

    /**
     * 相关分类推荐
     */
    private List<String> relatedCategories;

    /**
     * 是否使用了语义匹配
     */
    private Boolean semanticMatch;
}
