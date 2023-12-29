package com.ll.medium.global.security.jwt.refreshToken.service;

import com.ll.medium.domain.member.member.repository.MemberRepository;
import com.ll.medium.global.security.jwt.refreshToken.entity.RefreshToken;
import com.ll.medium.global.security.jwt.refreshToken.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final MemberRepository memberRepository;

    @Value("${jwt.jwtRefreshExpirationMs}")
    private Long refreshTokenDurationMs;

    @Transactional
    public RefreshToken create(Long id) {
        return refreshTokenRepository.save(RefreshToken.builder()
                .member(memberRepository.findById(id).get())
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(refreshTokenDurationMs))
                .build());
    }

    public Optional<RefreshToken> findByMemberId(Long memberId) {
        return refreshTokenRepository.findByMemberId(memberId);
    }

    @Transactional
    public int deleteByUserId(Long userId) {
        return refreshTokenRepository.deleteByMember(memberRepository.findById(userId).get());
    }
}
