package com.blog.api.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blog.api.entity.Category;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}