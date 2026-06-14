package com.blog.api.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CategoryVO {
    private Integer id;
    private String name;
    private String description;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
