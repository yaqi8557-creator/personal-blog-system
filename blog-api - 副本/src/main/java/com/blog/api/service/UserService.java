package com.blog.api.service;

import com.blog.api.entity.User;

public interface UserService {
    //接口中的方法默认是public
    User register(String username,String password,String nickname);
    String login (String username,String password);
    User getUserById(Integer id);
}
