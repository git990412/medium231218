package com.ll.medium.global.security.jwt;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class JwtUtils {
    @Value("${jwt.expiration}")
    private int jwtExpirationMs;

    @Value("${jwt.jwtRefreshExpirationMs}")
    private int refreshExpirationMs;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.jwtCookieName}")
    private String jwtCookie;

    @Value("${jwt.jwtRefreshCookieName}")
    private String jwtRefreshCookie;

    private SecretKey key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    public ResponseCookie generateJwtCookie(String email) {
        String jwt = generateTokenFromEmail(email);
        return generateCookie(jwtCookie, jwt, "/api");
    }

    private ResponseCookie generateCookie(String name, String value, String path) {
        return ResponseCookie.from(name, value)
                .path(path)
                .maxAge(14 * 24 * 60 * 60)
                .httpOnly(true)
                .build();
    }

    public ResponseCookie generateRefreshJwtCookie(String refreshToken) {
        return generateCookie(jwtRefreshCookie, refreshToken, "/api/v1/members/refreshtoken");
    }

    public String generateTokenFromEmail(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key())
                .compact();
    }

    public boolean validateJwtToken(String authToken) {
        Jwts.parser().verifyWith(key()).build().parse(authToken);
        return true;
    }

    public String getEmailFromJwtToken(String token) {
        return Jwts.parser()
                .verifyWith(key())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public String getJwtRefreshFromCookies(HttpServletRequest request) {
        return getCookieValueByName(request, jwtRefreshCookie);
    }

    public String getJwtFromCookies(HttpServletRequest request) {
        return getCookieValueByName(request, jwtCookie);
    }

    private String getCookieValueByName(HttpServletRequest request, String name) {
        Cookie cookie = WebUtils.getCookie(request, name);
        if (cookie != null) {
            return cookie.getValue();
        } else {
            return null;
        }
    }

    public ResponseCookie getCleanJwtCookie() {
        return ResponseCookie.from(jwtCookie, null)
                .path("/api")
                .maxAge(0)
                .build();
    }

    public ResponseCookie getCleanJwtRefreshCookie() {
        return ResponseCookie.from(jwtRefreshCookie, null)
                .path("/api/v1/members/refreshtoken")
                .maxAge(0)
                .build();
    }
}
