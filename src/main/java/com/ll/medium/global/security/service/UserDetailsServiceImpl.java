package com.ll.medium.global.security.service;

import org.hibernate.Hibernate;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ll.medium.domain.member.member.entity.Member;
import com.ll.medium.domain.member.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
        private final MemberRepository memberRepository;

        @Override
        @Transactional
        public UserDetailsImpl loadUserByUsername(String email) {
                Member member = memberRepository.findByEmail(email)
                                .orElseThrow(() -> new UsernameNotFoundException(
                                                "User Not Found with email: " + email));

                Hibernate.initialize(member.getRoles());

                UserDetailsImpl userDetails = new UserDetailsImpl(member);
                userDetails.setRoles(member.getRoles());

                return userDetails;
        }

}
