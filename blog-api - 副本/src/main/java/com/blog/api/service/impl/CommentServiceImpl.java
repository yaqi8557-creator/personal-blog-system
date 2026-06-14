package com.blog.api.service.impl;

import com.blog.api.exception.BusinessException;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.blog.api.entity.Comment;
import com.blog.api.entity.User;
import com.blog.api.Mapper.CommentMapper;
import com.blog.api.Mapper.UserMapper;
import com.blog.api.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public List<Comment> getCommentsByArticleId(Integer articleId) {
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getArticleId, articleId)
               .orderByAsc(Comment::getCreateTime);
        List<Comment> comments = commentMapper.selectList(wrapper);

        // 给每条评论补上用户的昵称
        for (Comment comment : comments) {
            User user = userMapper.selectById(comment.getUserId());
            if (user != null) {
                comment.setNickname(user.getNickname());
            }
        }
        return comments;
    }

    @Override
    public Comment addComment(Comment comment, Integer userId) {
        comment.setUserId(userId);
        commentMapper.insert(comment);
        return comment;
    }

    @Override
    public void deleteComment(Integer id, Integer userId) {
        Comment comment = commentMapper.selectById(id);
        if (comment == null) {
            throw new BusinessException("评论不存在");
        }
        // 只能删除自己的评论
        if (!comment.getUserId().equals(userId)) {
            throw new BusinessException("无权删除此评论");
        }
        commentMapper.deleteById(id);
    }
}
