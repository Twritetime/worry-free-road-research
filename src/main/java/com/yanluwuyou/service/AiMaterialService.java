package com.yanluwuyou.service;

import com.yanluwuyou.dto.AiSearchResultDTO;
import com.yanluwuyou.dto.AiTagResultDTO;
import com.yanluwuyou.entity.Material;

import java.util.List;

/**
 * AI资料智能服务接口
 */
public interface AiMaterialService {

    /**
     * AI智能搜索（语义搜索）
     */
    AiSearchResultDTO smartSearch(String keyword, Long userId);

    /**
     * 为资料自动生成标签和分类
     */
    AiTagResultDTO autoGenerateTags(Long materialId);

    /**
     * 批量为资料生成标签
     */
    List<AiTagResultDTO> batchGenerateTags(List<Long> materialIds);

    /**
     * 获取相似资料
     */
    List<Material> getSimilarMaterials(Long materialId, int limit);
}
