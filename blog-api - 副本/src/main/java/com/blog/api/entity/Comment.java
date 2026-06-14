package com.blog.api.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("comment")
public class Comment {

    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer articleId;
    private Integer userId;
    private String content;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    // 非数据库字段 —— 用于展示评论者的昵称
    @TableField(exist = false)
    private String nickname;
}
