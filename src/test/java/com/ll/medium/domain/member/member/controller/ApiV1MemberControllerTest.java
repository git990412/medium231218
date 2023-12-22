package com.ll.medium.domain.member.member.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post; // Import the necessary class
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print; // Import the necessary class
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Date;
import java.util.List;

import javax.crypto.SecretKey;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ll.medium.domain.member.member.entity.Member;
import com.ll.medium.domain.member.member.form.JoinForm;
import com.ll.medium.domain.member.member.form.LoginForm;
import com.ll.medium.domain.member.member.repository.MemberRepository;
import com.ll.medium.global.security.jwt.JwtUtils;
import com.ll.medium.global.security.jwt.refreshToken.repository.RefreshTokenRepository;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.Cookie;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ApiV1MemberControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    @Autowired
    private EntityManager entityManager;

    private final String email = "wjdwn@gmail.com";
    private final String password = "1234";
    private Cookie[] cookies = new Cookie[2];

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.jwtCookieName}")
    private String jwtCookie;

    @Test
    @DisplayName("signup 테스트")
    @Rollback(value = false)
    @Order(1)
    void joinTest() throws Exception {
        JoinForm joinForm = new JoinForm();
        joinForm.setEmail(email);
        joinForm.setPassword(password);
        joinForm.setUsername("test2");
        joinForm.setPasswordConfirm(password);

        mockMvc.perform(post("/api/v1/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(joinForm)))
                .andDo(print()); // Fix the problem by using the print() method from the MockMvcResultHandlers
                                 // class

        entityManager.flush();
    }

    @Test
    @DisplayName("로그인 테스트")
    @Rollback(value = false)
    void loginTest() throws Exception {
        LoginForm loginForm = new LoginForm();
        loginForm.setEmail(email);
        loginForm.setPassword(password);

        List<Member> members = memberRepository.findAll();

        mockMvc.perform(post("/api/v1/members/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginForm)))
                .andExpect(status().isOk())
                .andDo(result -> cookies = result.getResponse().getCookies());
    }

    @Test
    @DisplayName("로그인 후 토근 만료 예외처리 테스트")
    void expiredJwtExceptionTest() throws Exception {
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));

        String tokenString = Jwts.builder()
                .subject(email)
                .expiration(new Date((new Date()).getTime() - 1000))
                .signWith(key)
                .compact();

        for (int i = 0; i < cookies.length; i++) {
            if (cookies[i].getName().equals(jwtCookie)) {
                cookies[i].setValue(tokenString);
            }
        }

        mockMvc.perform(get("/api/v1/members/test")
                .cookie(cookies))
                .andExpect(content().string("test"))
                .andDo(result -> cookies = result.getResponse().getCookies());
    }

    @Test
    @DisplayName("로그인 후 로그인 예외처리 테스트")
    void loginExceptionTest() throws Exception {
        LoginForm loginForm = new LoginForm();
        loginForm.setEmail(email);
        loginForm.setPassword(password);

        mockMvc.perform(post("/api/v1/members/login")
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(cookies)
                .content(objectMapper.writeValueAsString(loginForm)))
                .andExpect(status().isForbidden())
                .andDo(print());
    }
}
