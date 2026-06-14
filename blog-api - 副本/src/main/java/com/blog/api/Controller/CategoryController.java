package com.blog.api.Controller;

import com.blog.api.Mapper.CategoryMapper;
import com.blog.api.common.R;
import com.blog.api.dto.CategoryDTO;
import com.blog.api.entity.Category;
import com.blog.api.util.RedisUtil;
import com.blog.api.vo.CategoryVO;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "文章分类模块")
@RestController
@RequestMapping("/api/category")
public class CategoryController {

    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private RedisUtil redisUtil;

    private static final String CACHE_KEY = "blog:category";

    @Operation(summary = "获取所有分类(带有缓存)")
    @GetMapping("/list")
    public R<List<CategoryVO>> list() {
        String cached = redisUtil.get(CACHE_KEY);
        if (cached != null) {
            List<Category> categories = JSON.parseObject(cached, new TypeReference<>() {});
            return R.success(categories.stream().map(this::toCategoryVO).collect(Collectors.toList()));
        }
        List<Category> categories = categoryMapper.selectList(null);
        redisUtil.set(CACHE_KEY, JSON.toJSONString(categories), 1800);
        return R.success(categories.stream().map(this::toCategoryVO).collect(Collectors.toList()));
    }

    @Operation(summary = "新增分类")
    @PostMapping
    public R<CategoryVO> save(@Valid @RequestBody CategoryDTO dto) {
        Category category = new Category();
        BeanUtils.copyProperties(dto, category);
        categoryMapper.insert(category);
        redisUtil.delete(CACHE_KEY);
        return R.success(toCategoryVO(category));
    }

    @Operation(summary = "修改分类")
    @PutMapping
    public R<CategoryVO> update(@RequestBody Category category) {
        categoryMapper.updateById(category);
        redisUtil.delete(CACHE_KEY);
        return R.success(toCategoryVO(categoryMapper.selectById(category.getId())));
    }

    @Operation(summary = "删除分类")
    @DeleteMapping("/{id}")
    public R<String> delete(@PathVariable Integer id) {
        categoryMapper.deleteById(id);
        redisUtil.delete(CACHE_KEY);
        return R.success("删除成功", null);
    }

    private CategoryVO toCategoryVO(Category category) {
        if (category == null) return null;
        CategoryVO vo = new CategoryVO();
        BeanUtils.copyProperties(category, vo);
        vo.setCreateTime(category.getCreate_time());
        
        return vo;
    }
}
