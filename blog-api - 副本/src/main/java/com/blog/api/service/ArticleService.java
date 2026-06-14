package com.blog.api.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.api.entity.Article;

import java.util.List;

public interface ArticleService {
    Page<Article> getArticlePage(Integer page, Integer size, Integer category_id);
    Article getArticleById(Integer id);
    Article createArticle(Article article, Integer author_id);
    Article updateArticle(Article article);
    void deleteArticle(Integer id);
    List<Article> selectList(com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Article> wrapper);
}
