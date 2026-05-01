package com.yanluwuyou.controller;

import com.yanluwuyou.auth.AuthGuard;
import com.yanluwuyou.common.Result;
import com.yanluwuyou.dto.AiSearchResultDTO;
import com.yanluwuyou.dto.AiTagResultDTO;
import com.yanluwuyou.entity.Material;
import com.yanluwuyou.service.AiMaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * AI资料智能服务控制器
 */
@RestController
@RequestMapping("/ai-material")
public class AiMaterialController {

    @Autowired
    private AiMaterialService aiMaterialService;

    /**
     * AI智能搜索（语义搜索）
     */
    @GetMapping("/search")
    public Result<AiSearchResultDTO> smartSearch(@RequestParam String keyword) {
        Long userId = AuthGuard.currentUserId();
        AiSearchResultDTO result = aiMaterialService.smartSearch(keyword, userId);
        return Result.success(result);
    }

    /**
     * 为资料自动生成标签
     */
    @PostMapping("/generate-tags/{materialId}")
    public Result<AiTagResultDTO> autoGenerateTags(@PathVariable Long materialId) {
        AiTagResultDTO result = aiMaterialService.autoGenerateTags(materialId);
        return Result.success(result);
    }

    /**
     * 批量生成标签
     */
    @PostMapping("/batch-generate-tags")
    public Result<List<AiTagResultDTO>> batchGenerateTags(@RequestBody List<Long> materialIds) {
        List<AiTagResultDTO> results = aiMaterialService.batchGenerateTags(materialIds);
        return Result.success(results);
    }

    /**
     * 获取相似资料
     */
    @GetMapping("/similar/{materialId}")
    public Result<List<Material>> getSimilarMaterials(
            @PathVariable Long materialId,
            @RequestParam(defaultValue = "5") int limit) {
        List<Material> materials = aiMaterialService.getSimilarMaterials(materialId, limit);
        return Result.success(materials);
    }
}
