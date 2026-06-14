package com.blog.api.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blog.api.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
}