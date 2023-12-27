package com.ll.medium.domain.post.post.controller;

import com.ll.medium.domain.post.like.service.LikeService;
import com.ll.medium.domain.post.post.dto.PostDto;
import com.ll.medium.domain.post.post.entity.Post;
import com.ll.medium.domain.post.post.form.WriteForm;
import com.ll.medium.domain.post.post.service.PostService;
import com.ll.medium.global.rq.Rq.Rq;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class ApiV1PostController {
    private final PostService postService;
    private final LikeService likeService;
    private final Rq rq;

    String uploadDir = System.getProperty("user.dir") + "/src/main/resources/static/images/";

    @GetMapping("/list")
    public ResponseEntity<Page<PostDto>> getList(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "sortCode", defaultValue = "") String sortCode,
            @RequestParam(value = "kwType", defaultValue = "") String kwType,
            @RequestParam(value = "kw", defaultValue = "") String kw) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("id"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        return ResponseEntity.ok().body(postService.findByKw(kwType, kw, pageable));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/myList")
    public ResponseEntity<Page<PostDto>> getMyList(@RequestParam(value = "page", defaultValue = "0") int page) {
        return ResponseEntity.ok().body(postService.getMyList(page, rq.getUser().getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDto> getOne(@PathVariable("id") long id) {
        Post post = postService.getOne(id);
        PostDto postDto = new PostDto(post);

        if (postDto.isPaid()) {
            if (rq.getUser() == null || !rq.getUser().isPaid()) {
                postDto.setBody("이 글은 유료멤버십전용 입니다.");
            }
        }

        return ResponseEntity.ok().body(postDto);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/write")
    public ResponseEntity<?> writePost(@Valid @RequestBody WriteForm form) {
        postService.writePost(form, rq.getUser().getId());

        return ResponseEntity.created(null).build();
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}/modify")
    public ResponseEntity<?> putMethodName(@PathVariable("id") Long id, @Valid @RequestBody WriteForm form) {

        Long MemberId = postService.getPostMemberId(id);

        if (rq.getUser().getId().equals(MemberId)) {
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

        if (rq.getUser().getId().equals(MemberId)) {
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
        if (likeService.isLiked(id, rq.getUser().getId())) {
            return ResponseEntity.badRequest().build();
        }

        likeService.createLike(id, rq.getUser().getId());

        return ResponseEntity.created(null).build();
    }

    @DeleteMapping("/{id}/cancelLike")
    public ResponseEntity<?> cancelLike(@PathVariable("id") Long id) {
        likeService.cancelLike(id, rq.getUser().getId());

        return ResponseEntity.ok().build();
    }

}
