package com.ll.medium.domain.member.member.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ll.medium.domain.member.member.dto.MemberDto;
import com.ll.medium.domain.member.member.entity.Member;
import com.ll.medium.domain.member.member.form.*;
import com.ll.medium.domain.member.member.service.MemberService;
import com.ll.medium.global.security.jwt.JwtUtils;
import com.ll.medium.global.security.jwt.refreshToken.entity.RefreshToken;
import com.ll.medium.global.security.jwt.refreshToken.service.RefreshTokenService;
import com.ll.medium.global.security.service.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class ApiV1MemberController {
    private final MemberService memberService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final JwtUtils jwtUtils;
    private final WebClient webClient = WebClient.create();

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

            RefreshToken refreshToken = refreshTokenService.findByMemberId(userDetails.getId())
                    .orElseGet(() -> refreshTokenService.create(userDetails.getId()));

            ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails.getEmail());
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

    @PreAuthorize("isAnonymous()")
    @PostMapping("/naver/login")
    public ResponseEntity<?> naverLogin(@RequestBody NaverLoginForm naverLoginRequest) throws JsonProcessingException {
        NaverToken nToken = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host("nid.naver.com")
                        .path("/oauth2.0/token")
                        .queryParam("code", naverLoginRequest.getCode())
                        .queryParam("state", naverLoginRequest.getState())
                        .queryParam("grant_type", naverLoginRequest.getGrantType())
                        .queryParam("client_id", naverLoginRequest.getClientId())
                        .queryParam("client_secret", naverLoginRequest.getClientSecret())
                        .build())
                .retrieve()
                .bodyToMono(NaverToken.class)
                .block();

        if (nToken != null) {
            NaverProfiles profile = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("https")
                            .host("openapi.naver.com")
                            .path("/v1/nid/me")
                            .build())
                    .header("Authorization", "Bearer " + nToken.getAccess_token())
                    .retrieve()
                    .bodyToMono(NaverProfiles.class)
                    .block();

            if (profile != null) {
                Member member = memberService.findByEmail(profile.getResponse().getEmail())
                        .orElseGet(() -> memberService.join(JoinForm.builder()
                                .email(profile.getResponse().getEmail())
                                .password(UUID.randomUUID().toString())
                                .username(profile.getResponse().getNickname())
                                .build()));

                RefreshToken refreshToken = refreshTokenService.findByMemberId(member.getId())
                        .orElseGet(() -> refreshTokenService.create(member.getId()));

                ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(member.getEmail());
                ResponseCookie jwtRefreshCookie = jwtUtils.generateRefreshJwtCookie(refreshToken.getToken());

                return ResponseEntity.ok()
                        .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                        .header(HttpHeaders.SET_COOKIE, jwtRefreshCookie.toString())
                        .body(MemberDto.builder().username(member.getUsername()).build());
            }
        }


        return ResponseEntity.badRequest().build();
    }

}