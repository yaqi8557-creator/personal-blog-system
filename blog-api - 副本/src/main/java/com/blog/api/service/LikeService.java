package com.blog.api.service;

public interface LikeService {
    boolean like(Integer articleId, Integer userId);
    boolean unlike(Integer articleId, Integer userId);
    boolean isLiked(Integer articleId, Integer userId);
    long getLikeCount(Integer articleId);
}
