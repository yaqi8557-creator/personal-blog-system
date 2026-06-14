package com.blog.api.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user")//告诉Mybatis-Plus这个类对应数据库表user
public class User {
    @TableId(type= IdType.AUTO)//标记主键：告诉 MyBatis-Plus 这个字段是数据库表的主键
    private Integer id ;
    private String username;
    private String nickname;
    private String password;
    private String email;
    private String avatar;
    @TableField(fill= FieldFill.INSERT)//@TableField：标记实体类属性对应数据库表字段。
    private LocalDateTime create_time;
    @TableField(fill=FieldFill.INSERT_UPDATE)
    private LocalDateTime update_time;
}
