package com.blog.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ArticleDTO {
    private Integer id;

    @NotBlank(message = "文章标题不能为空")
    private String title;

    @NotBlank(message = "文章内容不能为空")
    private String content;

    private String summary;

    @NotNull(message = "请选择文章分类")
    private Integer categoryId;
}
