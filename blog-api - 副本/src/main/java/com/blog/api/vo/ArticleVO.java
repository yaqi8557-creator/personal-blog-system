package com.blog.api.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ArticleVO {
    private Integer id;
    private String title;
    private String content;
    private String summary;
    private Integer categoryId;
    private String categoryName;
    private Integer authorId;
    private String authorName;
    private Integer viewCount;
    private Integer likeCount;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
