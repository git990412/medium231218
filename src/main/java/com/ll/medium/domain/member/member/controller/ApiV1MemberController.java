package com.ll.medium.domain.member.member.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ll.medium.domain.member.member.form.JoinForm;
import com.ll.medium.domain.member.member.form.LoginForm;
import com.ll.medium.domain.member.member.service.MemberService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class ApiV1MemberController {
    private final MemberService memberService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("")
    public ResponseEntity<?> join(@Valid @RequestBody JoinForm joinForm) {
        if (joinForm.getPassword().equals(joinForm.getPasswordConfirm())) {
            memberService.join(joinForm);
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.badRequest().body("비밀번호가 일치하지 않습니다.");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginForm loginForm) {
        if (!memberService.findByEmail(loginForm.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("아이디 또는 비밀번호가 일치하지 않습니다.");
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginForm.getEmail(), loginForm.getPassword()));

        return ResponseEntity.badRequest().body("아이디 또는 비밀번호가 일치하지 않습니다.");
    }
}