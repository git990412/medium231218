package com.ll.medium.global.security.jwt;

import java.io.IOException;
import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.ll.medium.domain.member.member.entity.Member;
import com.ll.medium.global.security.jwt.refreshToken.entity.RefreshToken;
import com.ll.medium.global.security.jwt.refreshToken.repository.RefreshTokenRepository;
import com.ll.medium.global.security.service.UserDetailsServiceImpl;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String jwt = parseJwt(request);
        try {
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                String email = jwtUtils.getEmailFromJwtToken(jwt);

                setAuth(request, email);
            }
        } catch (ExpiredJwtException e) {
            String refreshJwt = jwtUtils.getJwtRefreshFromCookies(request);
            RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshJwt).orElseGet(() -> null);
            if (refreshToken != null && refreshToken.getExpiryDate().isAfter(Instant.now())) {
                Member member = refreshToken.getMember();

                setAuth(request, member.getEmail());

                ResponseCookie jwtToken = jwtUtils.generateJwtCookie(member.getEmail());
                response.addHeader("Set-Cookie", jwtToken.toString());
            } else {
                ResponseCookie deleteJwtToken = jwtUtils.getCleanJwtCookie();
                ResponseCookie deleteRefreshToken = jwtUtils.getCleanJwtRefreshCookie();

                response.addHeader("Set-Cookie", deleteJwtToken.toString());
                response.addHeader("Set-Cookie", deleteRefreshToken.toString());
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            }
        }

        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        return jwtUtils.getJwtFromCookies(request);
    }

    private void setAuth(HttpServletRequest request, String email) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
