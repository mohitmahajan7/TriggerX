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
        email = email.trim().toLowerCase();
        String otp = genrateOtp();
        String key = key(email);
        redisTemplate.opsForValue().set(key, otp, OTP_TTL);

        // reset attempts on resend / new OTP
        redisTemplate.delete(attemptKey(email));

        return otp;
    }

    public String read(String email) {
        email = email.trim().toLowerCase();
        String key = key(email);
        String otp = redisTemplate.opsForValue().get(key);
        return otp;
    }


    public void delete(String email) {
        redisTemplate.delete(key(email));
        redisTemplate.delete(attemptKey(email));

    }

    private String genrateOtp(){
        return String.valueOf(100_00 + RANDOM.nextInt(900_000));

    }

    private String attemptKey(String email) {
        return "auth:otp:attempt:" + email;
    }

    private String normalize(String email) {
        return email.trim().toLowerCase();
    }

    private String key(String email) {
        return "auth:otp:register:" + normalize(email);
    }


    // ================= ATTEMPTS =================

    public void incrementAttempt(String email) {
        Long attempts = redisTemplate.opsForValue().increment(attemptKey(email));

        if (attempts != null && attempts == 1) {
            redisTemplate.expire(attemptKey(email), OTP_TTL);
        }
    }

    public boolean isLocked(String email) {
        String val = redisTemplate.opsForValue().get(attemptKey(email));
        if (val == null) return false;
        return Integer.parseInt(val) >= MAX_ATTEMPTS;
    }

    public int attemptsLeft(String email) {
        String val = redisTemplate.opsForValue().get(attemptKey(email));
        int used = val == null ? 0 : Integer.parseInt(val);
        return Math.max(0, MAX_ATTEMPTS - used);
    }

}
