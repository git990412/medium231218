package com.ll.medium.global.security.jwt.refreshToken.service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ll.medium.domain.member.member.repository.MemberRepository;
import com.ll.medium.global.security.jwt.refreshToken.entity.RefreshToken;
import com.ll.medium.global.security.jwt.refreshToken.repository.RefreshTokenRepository;
import com.ll.medium.global.security.service.UserDetailsImpl;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final MemberRepository memberRepository;

    @Value("${jwt.jwtRefreshExpirationMs}")
    private Long refreshTokenDurationMs;

    public RefreshToken create(UserDetailsImpl userdetails) {
        return refreshTokenRepository.save(RefreshToken.builder()
                .member(memberRepository.findById(userdetails.getId()).get())
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(refreshTokenDurationMs))
                .build());
    }
}
