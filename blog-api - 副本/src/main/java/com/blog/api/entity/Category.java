package com.blog.api.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("category")
public class Category {
    @TableId(type= IdType.AUTO)
    private Integer id;
    private String name;
    private String description;
    @TableField(fill= FieldFill.INSERT)
    private LocalDateTime create_time;
}
