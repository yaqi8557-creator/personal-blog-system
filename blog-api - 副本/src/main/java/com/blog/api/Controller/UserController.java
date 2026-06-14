package com.blog.api.Controller;

import com.blog.api.common.R;
import com.blog.api.dto.LoginDTO;
import com.blog.api.dto.RegisterDTO;
import com.blog.api.entity.User;
import com.blog.api.service.UserService;
import com.blog.api.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "用户模块")
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public R<UserVO> register(@Valid @RequestBody RegisterDTO dto) {
        User user = userService.register(dto.getUsername(), dto.getPassword(), dto.getNickname());
        return R.success(toUserVO(user));
    }

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public R<String> login(@Valid @RequestBody LoginDTO dto) {
        String token = userService.login(dto.getUsername(), dto.getPassword());
        return R.success(token);
    }

    @Operation(summary = "获取用户信息")
    @GetMapping("/info")
    public R<UserVO> info(HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        User user = userService.getUserById(userId);
        return R.success(toUserVO(user));
    }

    private UserVO toUserVO(User user) {
        if (user == null) return null;
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        vo.setCreateTime(user.getCreate_time());
        return vo;
    }
}
