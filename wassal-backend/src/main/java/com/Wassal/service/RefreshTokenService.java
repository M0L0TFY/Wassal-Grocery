package com.Wassal.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/*
* Create Refresh Token stored as a hash in redis
* Generate Refresh Token stored in httpOnly cookie, and an empty cookie for logout
*/

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final StringRedisTemplate redisTemplate;
    @Value("${security.jwt.refresh-token.expiration-ms}")
    private long refreshTokenDurationMs;
    @Value("${security.jwt.cookie-name}")
    private String cookieName;
    @Value("${security.jwt.cookie-secure}")
    private boolean cookieSecure;

    //Create refresh token that is stored in redis
    public String createRefreshToken(Long userId) {
        //Generate random refresh token
        String rawToken = UUID.randomUUID().toString();
        String hashedToken = hashToken(rawToken);

        //Save to Redis Key = hashedToken, Value = userId
        redisTemplate.opsForValue().set(
                hashedToken,
                String.valueOf(userId),
                refreshTokenDurationMs,
                TimeUnit.MILLISECONDS
        );
        return rawToken;
    }

    //Extract the user id from the refresh token
    public Optional<Long> findUserIdByToken(String rawToken) {
        //Hash the rawToken to compare in redis
        String hashedToken = hashToken(rawToken);
        String userIdStr = redisTemplate.opsForValue().get(hashedToken);
        //Convert String user Id to Long
        return Optional.ofNullable(userIdStr).map(Long::parseLong);
    }

    public void deleteByToken(String rawToken) {
        String hashedToken = hashToken(rawToken);
        redisTemplate.delete(hashedToken);
    }

    public ResponseCookie generateRefreshTokenCookie(String refreshToken) {
        return ResponseCookie.from(cookieName, refreshToken)
                .path("/api/v1/auth")
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite("Strict")
                .maxAge(refreshTokenDurationMs / 1000)
                .build();
    }

    public ResponseCookie getEmptyRefreshTokenCookie() {
        return ResponseCookie.from(cookieName, "")
                .path("/api/v1/auth")
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite("Strict")
                .maxAge(0)
                .build();
    }

    //Hash the token in case of a database leak
    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing token", e);
        }
    }
}
