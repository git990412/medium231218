package com.ll.medium.domain.post.post.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ll.medium.domain.post.post.dto.PostDto;
import com.ll.medium.domain.post.post.form.WriteForm;
import com.ll.medium.domain.post.post.service.PostService;
import com.ll.medium.global.security.service.UserDetailsImpl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class ApiV1PostController {
    private final PostService postService;

    private UserDetailsImpl getUserDetails() {
        return (UserDetailsImpl) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }

    @GetMapping("/list")
    public ResponseEntity<Page<PostDto>> getList(@RequestParam(value = "page", defaultValue = "0") int page) {
        return ResponseEntity.ok().body(postService.getList(page));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/myList")
    public ResponseEntity<Page<PostDto>> getMyList(@RequestParam(value = "page", defaultValue = "0") int page) {
        return ResponseEntity.ok().body(postService.getMyList(page, getUserDetails().getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDto> getOne(@PathVariable("id") long id) {
        PostDto postDto = postService.getOne(id);

        if (postDto.isPaid() && !getUserDetails().isPaid()) {
            postDto.setBody("이 글은 유료멤버십전용 입니다.");
        }

        return ResponseEntity.ok().body(postDto);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/write")
    public ResponseEntity<?> writePost(@Valid @RequestBody WriteForm form) {
        postService.writePost(form, getUserDetails().getId());

        return ResponseEntity.created(null).build();
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}/modify")
    public ResponseEntity<?> putMethodName(@PathVariable("id") Long id, @Valid @RequestBody WriteForm form) {

        Long MemberId = postService.getPostMemberId(id);

        if (getUserDetails().getId() == MemberId) {
            postService.updatePost(form, id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<?> deletePost(@PathVariable("id") Long id) {
        Long MemberId = postService.getPostMemberId(id);

        if (getUserDetails().getId() == MemberId) {
            postService.deletePost(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }
}
