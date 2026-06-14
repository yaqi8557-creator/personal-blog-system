package com.blog.api.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserVO {
    private Integer id;
    private String username;
    private String nickname;
    private String email;
    private String avatar;
    private LocalDateTime createTime;
}
