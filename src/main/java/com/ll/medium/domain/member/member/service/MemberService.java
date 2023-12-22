package com.ll.medium.domain.member.member.service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ll.medium.domain.member.member.entity.Member;
import com.ll.medium.domain.member.member.form.JoinForm;
import com.ll.medium.domain.member.member.repository.MemberRepository;
import com.ll.medium.domain.member.role.entity.ERole;
import com.ll.medium.domain.member.role.entity.Role;
import com.ll.medium.domain.member.role.repository.RoleRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Transactional
    public void join(JoinForm joinForm) {
        Set<Role> roles = new HashSet<>();
        roles.add(roleRepository.findByName(ERole.ROLE_USER));

        memberRepository.save(Member.builder()
                .username(joinForm.getUsername())
                .password(passwordEncoder.encode(joinForm.getPassword()))
                .email(joinForm.getEmail())
                .roles(roles)
                .build());
    }

    public Optional<Member> findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }
}
