package com.ll.medium.global.initData;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.IntStream;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ll.medium.domain.member.member.entity.Member;
import com.ll.medium.domain.member.member.repository.MemberRepository;
import com.ll.medium.domain.member.role.entity.ERole;
import com.ll.medium.domain.member.role.entity.Role;
import com.ll.medium.domain.member.role.repository.RoleRepository;
import com.ll.medium.domain.post.post.entity.Post;
import com.ll.medium.domain.post.post.repository.PostRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NotProd implements ApplicationRunner {
    private final MemberRepository memberRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final PostRepository postRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        if (memberRepository.count() == 0) {
            Set<Role> roles = new HashSet<>();
            roles.add(roleRepository.findByName(ERole.ROLE_USER));

            Set<Role> roles2 = new HashSet<>();
            roles2.add(roleRepository.findByName(ERole.ROLE_USER));
            roles2.add(roleRepository.findByName(ERole.ROLE_PAID));

            IntStream.rangeClosed(1, 3)
                    .forEach(i -> {
                        memberRepository.save(
                                Member.builder()
                                        .username("user" + i)
                                        .email("user" + i + "@gmail.com")
                                        .password(passwordEncoder.encode("1234"))
                                        .roles(roles)
                                        .build());
                    });

            IntStream.rangeClosed(4, 105)
                    .forEach(i -> {
                        memberRepository.save(
                                Member.builder()
                                        .username("user" + i)
                                        .email("user" + i + "@gmail.com")
                                        .password(passwordEncoder.encode("1234"))
                                        .roles(roles2)
                                        .isPaid(true)
                                        .build());
                    });
        }

        if (postRepository.count() == 0) {
            IntStream.rangeClosed(1, 300)
                    .forEach(
                            i -> {
                                if (i <= 100) {
                                    postRepository.save(
                                            Post.builder()
                                                    .title("title" + i)
                                                    .body("content" + i)
                                                    .member(memberRepository.findById(1L).get())
                                                    .isPublished(true)
                                                    .isPaid(true)
                                                    .build());
                                } else if (i <= 200) {
                                    postRepository.save(
                                            Post.builder()
                                                    .title("title" + i)
                                                    .body("content" + i)
                                                    .member(memberRepository.findById(2L).get())
                                                    .isPublished(true)
                                                    .build());
                                } else {
                                    postRepository.save(
                                            Post.builder()
                                                    .title("title" + i)
                                                    .body("content" + i)
                                                    .member(memberRepository.findById(3L).get())
                                                    .build());
                                }
                            });
        }

    }
}
