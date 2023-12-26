package com.ll.medium.domain.post.post.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ll.medium.domain.post.post.dto.PostDto;
import com.ll.medium.domain.post.post.service.PostService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class ApiV1PostController {
    private final PostService postService;

    @GetMapping("/list")
    public ResponseEntity<Page<PostDto>> getList(@RequestParam(value = "page", defaultValue = "0") int page) {
        return ResponseEntity.ok().body(postService.getList(page));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDto> getOne(@PathVariable("id") long id) {
        return ResponseEntity.ok().body(postService.getOne(id));
    }
}
