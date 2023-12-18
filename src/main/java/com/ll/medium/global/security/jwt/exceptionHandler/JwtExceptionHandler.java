package com.ll.medium.global.security.jwt.exceptionHandler;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.ll.medium.global.security.jwt.JwtFilter;
import com.ll.medium.global.security.jwt.JwtUtils;
import com.ll.medium.global.security.jwt.refreshToken.entity.RefreshToken;
import com.ll.medium.global.security.jwt.refreshToken.repository.RefreshTokenRepository;
import com.ll.medium.global.security.service.UserDetailsServiceImpl;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@ControllerAdvice(basePackageClasses = JwtFilter.class)
@RequiredArgsConstructor
public class JwtExceptionHandler {
    private final JwtUtils jwtUtils;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserDetailsServiceImpl userDetailsService;

    @ExceptionHandler(ExpiredJwtException.class)
    public void handleExpiredJwtException(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws IOException, ServletException {
        String token = jwtUtils.getJwtRefreshFromCookies(request);
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token).orElse(null);

        if (refreshToken != null) {
            String username = refreshToken.getMember().getUsername();
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            Cookie jwtCookie = jwtUtils.generateJwtCookie(username);
            response.addCookie(jwtCookie);

            filterChain.doFilter(request, response);
        } else {
            response.sendError(401, "로그인이 필요합니다.");
        }
    }
}
