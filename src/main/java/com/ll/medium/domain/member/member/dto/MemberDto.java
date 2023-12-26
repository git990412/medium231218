package com.ll.medium.domain.member.member.dto;

import com.ll.medium.domain.member.member.entity.Member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class MemberDto {
    private String username;
    private boolean isPaid;

    public MemberDto(Member member) {
        this.username = member.getUsername();
        this.isPaid = member.isPaid();
    }
}
