package com.blog.api.service.impl;

import com.blog.api.service.LikeService;
import com.blog.api.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LikeServiceImpl implements LikeService {

    @Autowired
    private RedisUtil redisUtil;

    private String getKey(Integer articleId) {
        return "article:" + articleId + ":likes";
    }

    @Override
    public boolean like(Integer articleId, Integer userId) {
        String key = getKey(articleId);
        if (redisUtil.sIsMember(key, userId.toString())) {
            return false;  // 已经点过了
        }
        redisUtil.sAdd(key, userId.toString());
        return true;
    }

    @Override
    public boolean unlike(Integer articleId, Integer userId) {
        String key = getKey(articleId);
        if (!redisUtil.sIsMember(key, userId.toString())) {
            return false;  // 还没点过赞，不能取消
        }
        redisUtil.sRemove(key, userId.toString());
        return true;
    }

    @Override
    public boolean isLiked(Integer articleId, Integer userId) {
        return redisUtil.sIsMember(getKey(articleId), userId.toString());
    }

    @Override
    public long getLikeCount(Integer articleId) {
        Long count = redisUtil.sSize(getKey(articleId));
        return count == null ? 0 : count;
    }
}
