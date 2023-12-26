package com.ll.medium.domain.post.post.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

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
import org.springframework.web.multipart.MultipartFile;

import com.ll.medium.domain.post.like.service.LikeService;
import com.ll.medium.domain.post.post.dto.PostDto;
import com.ll.medium.domain.post.post.entity.Post;
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
    private final LikeService likeService;

    String uploadDir = System.getProperty("user.dir") + "/src/main/resources/static/images/";

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
        Post post = postService.getOne(id);
        PostDto postDto = new PostDto(post);

        if (!post.getMember().getId().equals(getUserDetails().getId())) {
            if (postDto.isPaid() && !getUserDetails().isPaid()) {
                postDto.setBody("이 글은 유료멤버십전용 입니다.");
            }
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

        if (getUserDetails().getId().equals(MemberId)) {
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

        if (getUserDetails().getId().equals(MemberId)) {
            postService.deletePost(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("{id}/hit")
    public ResponseEntity<?> increaseHit(@PathVariable("id") Long id) {
        postService.increaseHit(id);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/imageUpload")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file)
            throws IllegalStateException, IOException {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("fail: no image resource");
        }

        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        // 파일을 저장할 경로 설정
        File destination = new File(uploadDir + fileName);

        // 파일 저장
        file.transferTo(destination);

        return ResponseEntity.ok().body("/api/v1/posts/getImage/" + fileName);
    }

    @GetMapping("/getImage/{fileName}")
    public ResponseEntity<byte[]> getMethodName(@PathVariable("fileName") String fileName) throws IOException {
        Path path = Paths.get(uploadDir + fileName);
        byte[] image = Files.readAllBytes(path);

        return ResponseEntity.ok().body(image);
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<?> doLike(@PathVariable("id") Long id) {
        likeService.createLike(id, getUserDetails().getId());

        return ResponseEntity.created(null).build();
    }

    @DeleteMapping("/{id}/cancelLike")
    public ResponseEntity<?> cancelLike(@PathVariable("id") Long id) {
        likeService.cancelLike(id, getUserDetails().getId());

        return ResponseEntity.ok().build();
    }

}
