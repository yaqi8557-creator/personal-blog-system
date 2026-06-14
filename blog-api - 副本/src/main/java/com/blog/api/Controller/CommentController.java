package com.blog.api.Controller;

import com.blog.api.common.R;
import com.blog.api.dto.CommentDTO;
import com.blog.api.entity.Comment;
import com.blog.api.service.CommentService;
import com.blog.api.util.JwtUtil;
import com.blog.api.vo.CommentVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "评论模块")
@RestController
@RequestMapping("/api/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Operation(summary = "获取文章评论列表")
    @GetMapping("/list/{articleId}")
    public R<List<CommentVO>> list(@PathVariable Integer articleId) {
        List<Comment> comments = commentService.getCommentsByArticleId(articleId);
        return R.success(comments.stream().map(this::toCommentVO).collect(Collectors.toList()));
    }

    @Operation(summary = "发表评论")
    @PostMapping
    public R<CommentVO> add(@Valid @RequestBody CommentDTO dto,
                            @RequestHeader("Authorization") String token) {
        Integer userId = JwtUtil.getId(token.replace("Bearer ", ""));
        Comment comment = new Comment();
        comment.setArticleId(dto.getArticleId());
        comment.setContent(dto.getContent());
        return R.success(toCommentVO(commentService.addComment(comment, userId)));
    }

    @Operation(summary = "删除评论")
    @DeleteMapping("/{id}")
    public R<String> delete(@PathVariable Integer id,
                            @RequestHeader("Authorization") String token) {
        Integer userId = JwtUtil.getId(token.replace("Bearer ", ""));
        commentService.deleteComment(id, userId);
        return R.success("删除成功", null);
    }

    private CommentVO toCommentVO(Comment comment) {
        if (comment == null) return null;
        CommentVO vo = new CommentVO();
        BeanUtils.copyProperties(comment, vo);
        vo.setCreateTime(comment.getCreateTime());
        return vo;
    }
}
