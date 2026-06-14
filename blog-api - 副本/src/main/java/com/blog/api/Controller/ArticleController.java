package com.blog.api.Controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.blog.api.Mapper.ArticleMapper;
import com.blog.api.Mapper.CategoryMapper;
import com.blog.api.common.R;
import com.blog.api.dto.ArticleDTO;
import com.blog.api.entity.Article;
import com.blog.api.entity.Category;
import com.blog.api.service.ArticleService;
import com.blog.api.util.RedisUtil;
import com.blog.api.vo.ArticleVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Tag(name = "文章模块")
@RestController
@RequestMapping("/api/article")
public class ArticleController {

    @Autowired
    private ArticleService articleService;
    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private RedisUtil redisUtil;

    @Operation(summary = "分页查询文章列表(带缓存)")
    @GetMapping("/list")
    public R<Page<ArticleVO>> list(
        @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") Integer page,
        @Parameter(description = "默认10条") @RequestParam(defaultValue = "10") Integer size,
        @Parameter(description = "分类ID,查询条件") @RequestParam(required = false) Integer categoryId) {

        String cacheKey = "blog:article:page:" + page + ":size:" + size;
        if (page == 1 && categoryId == null) {
            String cached = redisUtil.get(cacheKey);
            if (cached != null) {
                Page<Article> pageObj = JSON.parseObject(cached, new TypeReference<>() {});
                Page<ArticleVO> voPage = convertArticlePage(pageObj);
                return R.success(voPage);
            }
        }

        Page<Article> result = articleService.getArticlePage(page, size, categoryId);
        if (page == 1 && categoryId == null) {
            redisUtil.set(cacheKey, JSON.toJSONString(result), 600);
        }
        return R.success(convertArticlePage(result));
    }

    @Operation(summary = "查看文章详情")
    @GetMapping("/{id}")
    public R<ArticleVO> detail(@PathVariable Integer id) {
        Article article = articleService.getArticleById(id);
        return R.success(toArticleVO(article));
    }

    @Operation(summary = "发布文章")
    @PostMapping
    public R<ArticleVO> create(@Valid @RequestBody ArticleDTO dto,
                               HttpServletRequest request) {
        Integer authorId = (Integer) request.getAttribute("userId");
        Article article = new Article();
        BeanUtils.copyProperties(dto, article);
        Article result = articleService.createArticle(article, authorId);
        redisUtil.delete("blog:article:page:1:size:10");
        return R.success(toArticleVO(result));
    }

    @Operation(summary = "修改文章")
    @PutMapping
    public R<ArticleVO> update(@Valid @RequestBody ArticleDTO dto) {
        Article article = new Article();
        BeanUtils.copyProperties(dto, article);
        Article result = articleService.updateArticle(article);
        redisUtil.delete("blog:article:page:1:size:10");
        return R.success(toArticleVO(result));
    }

    @Operation(summary = "删除文章")
    @DeleteMapping("/{id}")
    public R<String> delete(@PathVariable Integer id) {
        articleService.deleteArticle(id);
        redisUtil.delete("blog:article:page:1:size:10");
        return R.success("删除成功", null);
    }

    @Operation(summary = "获取热门文章(按点赞数排序)")
    @GetMapping("/hot")
    public R<List<ArticleVO>> hot() {
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Article::getLike_count).last("LIMIT 10");
        List<Article> list = articleService.selectList(wrapper);
        return R.success(list.stream().map(this::toArticleVO).collect(Collectors.toList()));
    }

    @Operation(summary = "获取站点统计数据")
    @GetMapping("/stats")
    public R<Map<String, Object>> stats() {
        Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("articles", articleMapper.selectCount(null));
        stats.put("categories", categoryMapper.selectCount(null));
        List<Article> all = articleMapper.selectList(null);
        stats.put("totalViews", all.stream().mapToLong(a -> a.getView_count() == null ? 0 : a.getView_count()).sum());
        stats.put("totalLikes", all.stream().mapToLong(a -> a.getLike_count() == null ? 0 : a.getLike_count()).sum());
        return R.success(stats);
    }

    @Operation(summary = "搜索文章（标题+内容模糊匹配）")
    @GetMapping("/search")
    public R<Page<ArticleVO>> search(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<Article> pageObj = new Page<>(page, size);
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(Article::getTitle, keyword)
               .or()
               .like(Article::getContent, keyword)
               .orderByDesc(Article::getCreate_time);
        return R.success(convertArticlePage(articleMapper.selectPage(pageObj, wrapper)));
    }

    private ArticleVO toArticleVO(Article article) {
        if (article == null) return null;
        ArticleVO vo = new ArticleVO();
        BeanUtils.copyProperties(article, vo);
        vo.setCategoryId(article.getCategory_id());
        vo.setAuthorId(article.getAuthor_id());
        vo.setViewCount(article.getView_count());
        vo.setLikeCount(article.getLike_count());
        vo.setCreateTime(article.getCreate_time());
        vo.setUpdateTime(article.getUpdate_time());
        // fill category name
        if (article.getCategory_id() != null) {
            Category category = categoryMapper.selectById(article.getCategory_id());
            if (category != null) {
                vo.setCategoryName(category.getName());
            }
        }
        return vo;
    }

    private Page<ArticleVO> convertArticlePage(Page<Article> page) {
        Page<ArticleVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        voPage.setRecords(page.getRecords().stream().map(this::toArticleVO).collect(Collectors.toList()));
        return voPage;
    }
}
