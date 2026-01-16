package org.stark.triggerxbackend.auth.otp;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.Duration;

@Component
public class OtpStore {

    private static final Duration OTP_TTL = Duration.ofMinutes(5);
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int MAX_ATTEMPTS = 5;

    private final StringRedisTemplate redisTemplate;

    public OtpStore(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public String generateAndStore(String email) {
        email = normalize(email);

        String otp = generateOtp();
        redisTemplate.opsForValue().set(otpKey(email), otp, OTP_TTL);
        redisTemplate.delete(attemptKey(email));

        return otp;
    }

    public String read(String email) {
        return redisTemplate.opsForValue().get(otpKey(normalize(email)));
    }

    public void delete(String email) {
        email = normalize(email);
        redisTemplate.delete(otpKey(email));
        redisTemplate.delete(attemptKey(email));
    }

    public void incrementAttempt(String email) {
        email = normalize(email);

        Long attempts = redisTemplate.opsForValue().increment(attemptKey(email));
        if (attempts != null && attempts == 1) {
            redisTemplate.expire(attemptKey(email), OTP_TTL);
        }
    }

    public boolean isLocked(String email) {
        String val = redisTemplate.opsForValue().get(attemptKey(normalize(email)));
        return val != null && Integer.parseInt(val) >= MAX_ATTEMPTS;
    }

    public int attemptsLeft(String email) {
        String val = redisTemplate.opsForValue().get(attemptKey(normalize(email)));
        int used = val == null ? 0 : Integer.parseInt(val);
        return Math.max(0, MAX_ATTEMPTS - used);
    }

    public long retryAfterSeconds(String email) {
        Long ttl = redisTemplate.getExpire(attemptKey(normalize(email)));
        return ttl == null || ttl < 0 ? 0 : ttl;
    }

    private String generateOtp() {
        return String.valueOf(100_000 + RANDOM.nextInt(900_000));
    }

    private String normalize(String email) {
        return email.trim().toLowerCase();
    }

    private String otpKey(String email) {
        return "auth:otp:register:" + email;
    }

    private String attemptKey(String email) {
        return "auth:otp:attempt:" + email;
    }
}
