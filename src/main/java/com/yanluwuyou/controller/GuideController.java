package com.yanluwuyou.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yanluwuyou.auth.RequireRoles;
import com.yanluwuyou.common.Result;
import com.yanluwuyou.entity.Guide;
import com.yanluwuyou.entity.User;
import com.yanluwuyou.service.GuideService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.net.URI;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 报考指南控制器
 */
@RestController
@RequestMapping("/guide")
public class GuideController {

    private static final Pattern YEAR_PATTERN = Pattern.compile("20\\d{2}");
    private static final Set<String> OFFICIAL_HOST_SUFFIXES = Set.of(
            "chsi.com.cn",
            "chsi.cn",
            "edu.cn",
            "gov.cn"
    );

    @Autowired
    private GuideService guideService;

    @Autowired
    private com.yanluwuyou.service.CrawlerService crawlerService;

    /**
     * 爬取指南数据
     */
    @PostMapping("/crawl")
    @RequireRoles({User.ROLE_ADMIN, User.ROLE_OPERATOR})
    public Result<?> crawl(@RequestParam String url, @RequestParam String category) {
        try {
            String normalizedUrl = normalizeCrawlUrl(url, category);
            int count = crawlerService.crawlGuides(normalizedUrl, category);
            if (count >= 0) {
                int removedCount = removeOutdatedGuides(category);
                return Result.success("成功爬取 " + count + " 条数据，清理旧年份 " + removedCount + " 条");
            }
            return Result.error("爬取失败，请检查URL或网络");
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取指南列表
     */
    @GetMapping("/list")
    public Result<Page<Guide>> list(@RequestParam(defaultValue = "1") Integer pageNum,
                                    @RequestParam(defaultValue = "10") Integer pageSize,
                                    @RequestParam(required = false) String keyword,
                                    @RequestParam(required = false) String category,
                                    @RequestParam(required = false) String institution,
                                    @RequestParam(required = false) String major,
                                    @RequestParam(required = false, defaultValue = "createTime") String sortBy,
                                    @RequestParam(required = false, defaultValue = "desc") String sortOrder) {
        Page<Guide> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Guide> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StrUtil.isNotBlank(keyword), Guide::getTitle, keyword);
        queryWrapper.eq(StrUtil.isNotBlank(category), Guide::getCategory, category);
        queryWrapper.like(StrUtil.isNotBlank(institution), Guide::getInstitution, institution);
        queryWrapper.like(StrUtil.isNotBlank(major), Guide::getMajor, major);
        queryWrapper.notLike(Guide::getTitle, "{{");
        queryWrapper.eq(Guide::getStatus, 1); // 仅显示已发布的指南

        if ("viewCount".equals(sortBy)) {
            if ("asc".equalsIgnoreCase(sortOrder)) {
                queryWrapper.orderByAsc(Guide::getViewCount).orderByDesc(Guide::getCreateTime);
            } else {
                queryWrapper.orderByDesc(Guide::getViewCount).orderByDesc(Guide::getCreateTime);
            }
        } else {
            if ("asc".equalsIgnoreCase(sortOrder)) {
                queryWrapper.orderByAsc(Guide::getCreateTime);
            } else {
                queryWrapper.orderByDesc(Guide::getCreateTime);
            }
        }

        return Result.success(guideService.page(page, queryWrapper));
    }

    /**
     * 获取所有指南列表 (管理端)
     */
    @GetMapping("/list-all")
    @RequireRoles({User.ROLE_ADMIN, User.ROLE_OPERATOR})
    public Result<Page<Guide>> listAll(@RequestParam(defaultValue = "1") Integer pageNum,
                                    @RequestParam(defaultValue = "10") Integer pageSize,
                                    @RequestParam(required = false) String keyword,
                                    @RequestParam(required = false) String category,
                                    @RequestParam(required = false) Integer status,
                                    @RequestParam(required = false) String institution,
                                    @RequestParam(required = false) String major) {
        Page<Guide> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Guide> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StrUtil.isNotBlank(keyword), Guide::getTitle, keyword);
        queryWrapper.eq(StrUtil.isNotBlank(category), Guide::getCategory, category);
        queryWrapper.like(StrUtil.isNotBlank(institution), Guide::getInstitution, institution);
        queryWrapper.like(StrUtil.isNotBlank(major), Guide::getMajor, major);
        queryWrapper.notLike(Guide::getTitle, "{{");
        if (status != null) {
            queryWrapper.eq(Guide::getStatus, status);
        }
        queryWrapper.orderByDesc(Guide::getSortOrder).orderByDesc(Guide::getCreateTime);
        
        return Result.success(guideService.page(page, queryWrapper));
    }

    /**
     * 更新指南状态
     */
    @PutMapping("/{id}/status/{status}")
    @RequireRoles({User.ROLE_ADMIN, User.ROLE_OPERATOR})
    public Result<?> updateStatus(@PathVariable Long id, @PathVariable Integer status) {
        Guide guide = guideService.getById(id);
        if (guide == null) {
            return Result.error("指南不存在");
        }
        guide.setStatus(status);
        guideService.updateById(guide);
        return Result.success();
    }

    /**
     * 根据ID获取指南详情
     */
    @GetMapping("/{id}")
    public Result<Guide> getById(@PathVariable Long id) {
        Guide guide = guideService.getById(id);
        if (guide != null) {
            if (containsTemplateToken(guide.getTitle()) || containsTemplateToken(guide.getContent())) {
                return Result.error("该指南数据异常，已被过滤，请重新爬取官方来源内容");
            }
            guideService.lambdaUpdate()
                    .eq(Guide::getId, id)
                    .setSql("view_count = IFNULL(view_count, 0) + 1")
                    .update();
            guide.setViewCount((guide.getViewCount() == null ? 0 : guide.getViewCount()) + 1);
            guide.setContent(sanitizeContent(guide.getContent()));
        }
        return Result.success(guide);
    }

    /**
     * 获取所有院校列表
     */
    @GetMapping("/institutions")
    public Result<List<String>> getInstitutions() {
        return Result.success(guideService.getDistinctInstitutions());
    }

    /**
     * 获取所有专业列表
     */
    @GetMapping("/majors")
    public Result<List<String>> getMajors() {
        return Result.success(guideService.getDistinctMajors());
    }

    /**
     * 新增指南
     */
    @PostMapping
    @RequireRoles({User.ROLE_ADMIN, User.ROLE_OPERATOR})
    public Result<?> save(@RequestBody Guide guide) {
        if (guide.getSortOrder() == null || guide.getSortOrder() == 0) {
            guide.setSortOrder(nextSortOrder());
        }
        guideService.save(guide);
        return Result.success();
    }

    /**
     * 更新指南
     */
    @PutMapping
    @RequireRoles({User.ROLE_ADMIN, User.ROLE_OPERATOR})
    public Result<?> update(@RequestBody Guide guide) {
        ensureSortOrder(guide);
        guideService.updateById(guide);
        return Result.success();
    }

    @PutMapping("/swap-order")
    @RequireRoles({User.ROLE_ADMIN, User.ROLE_OPERATOR})
    public Result<?> swapOrder(@RequestParam Long id, @RequestParam Long swapId) {
        if (id.equals(swapId)) {
            return Result.success();
        }
        Guide current = guideService.getById(id);
        Guide target = guideService.getById(swapId);
        if (current == null || target == null) {
            return Result.error("指南不存在");
        }
        if (current.getSortOrder() == null || current.getSortOrder() == 0) {
            current.setSortOrder(current.getId().intValue());
        }
        if (target.getSortOrder() == null || target.getSortOrder() == 0) {
            target.setSortOrder(target.getId().intValue());
        }
        Integer tempSort = current.getSortOrder();
        current.setSortOrder(target.getSortOrder());
        target.setSortOrder(tempSort);
        guideService.updateById(current);
        guideService.updateById(target);
        return Result.success();
    }

    /**
     * 删除指南
     */
    @DeleteMapping("/{id}")
    @RequireRoles({User.ROLE_ADMIN, User.ROLE_OPERATOR})
    public Result<?> delete(@PathVariable Long id) {
        guideService.removeById(id);
        return Result.success();
    }

    private Integer nextSortOrder() {
        Guide top = guideService.lambdaQuery()
                .orderByDesc(Guide::getSortOrder)
                .orderByDesc(Guide::getId)
                .last("limit 1")
                .one();
        int base = 0;
        if (top != null && top.getSortOrder() != null) {
            base = top.getSortOrder();
        }
        return base + 1;
    }

    private void ensureSortOrder(Guide guide) {
        if (guide.getSortOrder() != null && guide.getSortOrder() != 0) {
            return;
        }
        Guide existing = guideService.getById(guide.getId());
        if (existing != null && existing.getSortOrder() != null && existing.getSortOrder() != 0) {
            guide.setSortOrder(existing.getSortOrder());
            return;
        }
        guide.setSortOrder(nextSortOrder());
    }

    private String sanitizeContent(String content) {
        if (StrUtil.isBlank(content)) {
            return "";
        }
        Document document = Jsoup.parseBodyFragment(content);
        for (Element anchor : document.select("a")) {
            String href = anchor.attr("abs:href");
            if (!isOfficialUrl(href)) {
                anchor.remove();
                continue;
            }
            anchor.attr("href", href);
        }
        for (Element img : document.select("img")) {
            String src = img.attr("abs:src");
            if (!isOfficialUrl(src)) {
                img.remove();
                continue;
            }
            img.attr("src", src);
        }
        return document.body().html();
    }

    private boolean isOfficialUrl(String url) {
        try {
            URI uri = URI.create(url);
            String host = uri.getHost();
            if (StrUtil.isBlank(host)) {
                return false;
            }
            String normalized = host.toLowerCase();
            for (String suffix : OFFICIAL_HOST_SUFFIXES) {
                if (normalized.equals(suffix) || normalized.endsWith("." + suffix)) {
                    return true;
                }
            }
            return false;
        } catch (Exception ignored) {
            return false;
        }
    }

    private boolean containsTemplateToken(String value) {
        return StrUtil.containsAny(value, "{{", "}}", "${", "item.");
    }

    private int removeOutdatedGuides(String category) {
        List<Guide> categoryGuides = guideService.lambdaQuery()
                .eq(Guide::getCategory, category)
                .list();
        if (categoryGuides.isEmpty()) {
            return 0;
        }
        int latestYear = categoryGuides.stream()
                .map(Guide::getTitle)
                .mapToInt(this::extractNewestYear)
                .filter(year -> year > 0)
                .max()
                .orElse(0);
        if (latestYear == 0) {
            return 0;
        }
        int minAllowedYear = Math.max(LocalDate.now().getYear() - 1, latestYear - 1);
        List<Long> staleIds = categoryGuides.stream()
                .filter(guide -> {
                    int year = extractNewestYear(guide.getTitle());
                    return year > 0 && year < minAllowedYear;
                })
                .map(Guide::getId)
                .collect(Collectors.toList());
        if (staleIds.isEmpty()) {
            return 0;
        }
        guideService.removeBatchByIds(staleIds);
        return staleIds.size();
    }

    private int extractNewestYear(String text) {
        if (StrUtil.isBlank(text)) {
            return 0;
        }
        int latestYear = 0;
        Matcher matcher = YEAR_PATTERN.matcher(text);
        while (matcher.find()) {
            int year = Integer.parseInt(matcher.group());
            if (year >= 2020 && year <= 2099 && year > latestYear) {
                latestYear = year;
            }
        }
        return latestYear;
    }

    private String normalizeCrawlUrl(String rawUrl, String category) {
        String normalized = StrUtil.trim(rawUrl)
                .replace("`", "")
                .replace("[", "")
                .replace("]", "")
                .replace("\"", "")
                .replace("'", "");
        if (StrUtil.isBlank(normalized)) {
            return "https://yz.chsi.com.cn/kyzx/";
        }
        if (!normalized.startsWith("http://") && !normalized.startsWith("https://")) {
            normalized = "https://" + normalized;
        }
        if ("fushixize".equals(category) && normalized.contains("yz.chsi.com.cn/kyzx/fsgg/")) {
            return "https://yz.chsi.com.cn/kyzx/";
        }
        return normalized;
    }
}
