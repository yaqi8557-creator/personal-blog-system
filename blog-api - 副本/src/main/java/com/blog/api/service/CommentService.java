package com.blog.api.service;

import com.blog.api.entity.Comment;
import java.util.List;

public interface CommentService {
    List<Comment> getCommentsByArticleId(Integer articleId);
    Comment addComment(Comment comment, Integer userId);
    void deleteComment(Integer id, Integer userId);
}
