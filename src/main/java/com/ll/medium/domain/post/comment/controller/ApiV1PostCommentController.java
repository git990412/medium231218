package com.ll.medium.domain.post.comment.controller;

import com.ll.medium.domain.post.comment.service.PostCommentService;
import com.ll.medium.global.rq.Rq.Rq;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/posts/{postId}/comments")
@RequiredArgsConstructor
public class ApiV1PostCommentController {
    private final PostCommentService postCommentService;
    private final Rq rq;

    @GetMapping("/{commentId}")
    public ResponseEntity<?> get(@PathVariable("commentId") long id) {
        return ResponseEntity.ok(postCommentService.getComment(id));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/write")
    public ResponseEntity<?> write(@PathVariable("postId") long postId, @RequestBody Map<String, String> body) {
        postCommentService.addComment(body.get("body"), postId, rq.getUser().getId());

        return ResponseEntity.created(null).build();
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{commentId}/modify")
    public ResponseEntity<?> modify(@PathVariable("commentId") long id, @RequestBody Map<String, String> body) {
        if (!rq.getUser().getId().equals(postCommentService.getComment(id).getMember().getId()))
            return ResponseEntity.badRequest().body("권한이 없습니다.");

        postCommentService.updateComment(id, body.get("body"));

        return ResponseEntity.ok().build();
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{commentId}/delete")
    public ResponseEntity<?> delete(@PathVariable("commentId") long id) {
        if (!rq.getUser().getId().equals(postCommentService.getComment(id).getMember().getId()))
            return ResponseEntity.badRequest().body("권한이 없습니다.");

        postCommentService.deleteComment(id);

        return ResponseEntity.ok().build();
    }
}
