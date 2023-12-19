package com.ll.medium.global.security.jwt.exceptionHandler;

import java.io.IOException;
import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
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
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@ControllerAdvice(basePackageClasses = JwtFilter.class)
@RequiredArgsConstructor
public class JwtExceptionHandler {
    private final JwtUtils jwtUtils;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserDetailsServiceImpl userDetailsService;

    @Value("${jwt.jwtRefreshExpirationMs}")
    private Long refreshTokenDurationMs;

    @ExceptionHandler(ExpiredJwtException.class)
    public void handleExpiredJwtException(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws IOException, ServletException {
        String token = jwtUtils.getJwtRefreshFromCookies(request);
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token).orElse(null);

        if (refreshToken != null && refreshToken.getExpiryDate().isBefore(Instant.now())) {
            String username = refreshToken.getMember().getUsername();
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(username);
            response.addHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString());

            refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
            refreshTokenRepository.save(refreshToken);
        }

        filterChain.doFilter(request, response);
    }
}
