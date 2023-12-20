package com.ll.medium.domain.member.member.controller;

import java.util.HashMap;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ll.medium.domain.member.member.dto.MemberDto;
import com.ll.medium.domain.member.member.form.JoinForm;
import com.ll.medium.domain.member.member.form.LoginForm;
import com.ll.medium.domain.member.member.service.MemberService;
import com.ll.medium.global.security.jwt.JwtUtils;
import com.ll.medium.global.security.jwt.refreshToken.entity.RefreshToken;
import com.ll.medium.global.security.jwt.refreshToken.service.RefreshTokenService;
import com.ll.medium.global.security.service.UserDetailsImpl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class ApiV1MemberController {
    private final MemberService memberService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final JwtUtils jwtUtils;

    @PreAuthorize("isAnonymous()")
    @PostMapping("")
    public ResponseEntity<?> join(@Valid @RequestBody JoinForm joinForm) {
        if (joinForm.getPassword().equals(joinForm.getPasswordConfirm())) {
            memberService.join(joinForm);
            return ResponseEntity.created(null).build();
        }

        HashMap<String, String> error = new HashMap<>();
        error.put("passwordConfirm", "비밀번호가 일치하지 않습니다.");
        return ResponseEntity.badRequest().body(error);
    }

    @PreAuthorize("isAnonymous()")
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginForm loginForm) {
        HashMap<String, String> error = new HashMap<>();
        error.put("password", "아이디 또는 비밀번호가 일치하지 않습니다.");

        if (!memberService.findByEmail(loginForm.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body(error);
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginForm.getEmail(), loginForm.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            RefreshToken refreshToken = refreshTokenService.create(userDetails);

            ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails.getUsername());
            ResponseCookie jwtRefreshCookie = jwtUtils.generateRefreshJwtCookie(refreshToken.getToken());

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                    .header(HttpHeaders.SET_COOKIE, jwtRefreshCookie.toString())
                    .body(MemberDto.builder().username(userDetails.getUsername()).build());
        } catch (AuthenticationException e) {
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        refreshTokenService.deleteByUserId(userDetails.getId());

        ResponseCookie jwt = jwtUtils.getCleanJwtCookie();
        ResponseCookie jwtRefresh = jwtUtils.getCleanJwtRefreshCookie();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwt.toString())
                .header(HttpHeaders.SET_COOKIE, jwtRefresh.toString())
                .build();
    }
}