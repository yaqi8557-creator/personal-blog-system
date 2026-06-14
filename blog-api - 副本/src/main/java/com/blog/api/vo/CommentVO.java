package com.blog.api.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CommentVO {
    private Integer id;
    private Integer articleId;
    private Integer userId;
    private String nickname;
    private String content;
    private LocalDateTime createTime;
}
