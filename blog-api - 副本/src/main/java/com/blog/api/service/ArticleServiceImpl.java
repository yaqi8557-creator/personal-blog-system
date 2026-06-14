package com.blog.api.service;

import com.blog.api.exception.BusinessException;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.api.Mapper.ArticleMapper;
import com.blog.api.entity.Article;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArticleServiceImpl implements ArticleService{
    @Autowired
    private ArticleMapper articleMapper;

    @Override
    public Page<Article> getArticlePage(Integer page,
                                        Integer size, Integer category_id) {
        Page<Article> pageobj = new Page<>(page,size);
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        if (category_id!=null){
            wrapper.eq(Article::getCategory_id,category_id);
        }
        wrapper.orderByDesc(Article::getCreate_time);
        return articleMapper.selectPage(pageobj,wrapper);
    }
    @Override
    public Article getArticleById(Integer id) {
        Article article =articleMapper.selectById(id);
        if(article==null){
            throw new BusinessException("文章不存在");
        }
        article.setView_count(article.getView_count()==null?1:article.getView_count()+1);
        articleMapper.updateById(article);
        return article;
    }
    @Override
    public Article createArticle(Article article, Integer author_id) {
        article.setAuthor_id(author_id);
        article.setView_count(0);
        article.setLike_count(0);
        articleMapper.insert(article);
        return article;
    }
    @Override
    public Article updateArticle(Article article) {
        Article exist =articleMapper.selectById(article.getId());
        if(exist==null){
            throw new BusinessException("文章不存在");
        }
        articleMapper.updateById(article);
        return articleMapper.selectById(article.getId());
    }
    @Override
    public void deleteArticle(Integer id) {
        articleMapper.deleteById(id);
    }

    @Override
    public List<Article> selectList(LambdaQueryWrapper<Article> wrapper) {
        return articleMapper.selectList(wrapper);
    }
}
