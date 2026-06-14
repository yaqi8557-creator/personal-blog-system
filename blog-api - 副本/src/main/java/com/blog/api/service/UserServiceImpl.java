package com.blog.api.service;

import com.blog.api.exception.BusinessException;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.blog.api.Mapper.UserMapper;
import com.blog.api.entity.User;
import com.blog.api.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    // 加密器：用来把明文密码变成密文
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public User register(String username, String password, String nickname) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);

        if (userMapper.selectOne(wrapper) != null) {
            throw new BusinessException("用户名已存在");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(encoder.encode(password));  // 加密存库
        user.setNickname(nickname);
        userMapper.insert(user);
        return user;
    }

    @Override
    public String login(String username, String password) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        User user = userMapper.selectOne(wrapper);
//根据用户名查询用户是否存在
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (!encoder.matches(password, user.getPassword())) {
            //用：对比 明文 和 加密串 是否匹配
            throw new BusinessException("密码错误");
        }

        // 登录成功后返回 JWT token
        return JwtUtil.generateToken(username,user.getId());
    }

    @Override
    public User getUserById(Integer id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        // 返回时不暴露密码
        user.setPassword(null);
        return user;
    }
}

