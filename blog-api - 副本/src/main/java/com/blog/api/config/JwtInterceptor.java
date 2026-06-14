package com.blog.api.config;

import com.blog.api.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        //放行OPTIONS请求，方便进行跨域请求
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            response.setStatus(401);
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write("{\"code\":401,\"message\":\"请先登录，小朋友\"}");
            return false;
        }
        try {
            String realtoken = token.replace("Bearer ", "");
            JwtUtil.verifyToken(realtoken);

            // 解析 Id，设置为userid,确保文章控制类和用户控制类可以直接用,
            // 存到 request 里，后面的Controller 直接用
            Integer userId = JwtUtil.getId(realtoken);
            request.setAttribute("userId", userId);

            return true;
        } catch (Exception e) {
            response.setStatus(401);
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write("{\"code\":401,\"message\":\"无效的token\"}");
            return false;
        }
    }

}
