package com.blog.api.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("article")
public class Article {
    @TableId(type= IdType.AUTO)
    private Integer id;
    private String title;
    private String content;
    private String summary;
    private Integer category_id;
    private Integer author_id;
    private Integer view_count;
    private Integer like_count;
    @TableField(fill= FieldFill.INSERT)
    private LocalDateTime create_time;
    @TableField(fill= FieldFill.INSERT_UPDATE)
    private LocalDateTime update_time;
}
