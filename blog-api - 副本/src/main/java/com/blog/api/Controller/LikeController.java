package com.blog.api.Controller;

import com.blog.api.common.R;
import com.blog.api.service.LikeService;
import com.blog.api.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "点赞模块")
@RestController
@RequestMapping("/api/like")
public class LikeController {

    @Autowired
    private LikeService likeService;

    @Operation(summary = "点赞文章")
    @PostMapping("/{articleId}")
    public R<Map<String, Object>> like(@PathVariable Integer articleId,
                        @RequestHeader("Authorization") String token) {
        Integer userId = JwtUtil.getId(token.replace("Bearer ", ""));
        boolean success = likeService.like(articleId, userId);
        long count = likeService.getLikeCount(articleId);

        Map<String, Object> result = new HashMap<>();
        result.put("liked", success);
        result.put("likeCount", count);
        return R.success(result);
    }

    @Operation(summary = "取消点赞")
    @DeleteMapping("/{articleId}")
    public R<Map<String, Object>> unlike(@PathVariable Integer articleId,
                          @RequestHeader("Authorization") String token) {
        Integer userId = JwtUtil.getId(token.replace("Bearer ", ""));
        likeService.unlike(articleId, userId);
        long count = likeService.getLikeCount(articleId);

        Map<String, Object> result = new HashMap<>();
        result.put("liked", false);
        result.put("likeCount", count);
        return R.success(result);
    }

    @Operation(summary = "查询是否已点赞 + 点赞数")
    @GetMapping("/status/{articleId}")
    public R<Map<String, Object>> status(@PathVariable Integer articleId,
                          @RequestHeader("Authorization") String token) {
        Integer userId = JwtUtil.getId(token.replace("Bearer ", ""));
        boolean liked = likeService.isLiked(articleId, userId);
        long count = likeService.getLikeCount(articleId);

        Map<String, Object> result = new HashMap<>();
        result.put("liked", liked);
        result.put("likeCount", count);
        return R.success(result);
    }
}
