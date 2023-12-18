package com.ll.medium.global.security.jwt.refreshToken.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ll.medium.domain.member.member.entity.Member;
import com.ll.medium.global.security.jwt.refreshToken.entity.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String refreshToken);

    int deleteByMember(Member member);
}
