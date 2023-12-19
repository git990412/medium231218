package com.ll.medium.domain.member.member.dto;

import com.ll.medium.domain.member.member.entity.Member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class MemberDto {
    private String username;

    public MemberDto(Member member) {
        this.username = member.getUsername();
    }
}
