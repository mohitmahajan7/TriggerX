package org.stark.triggerxbackend.auth.otp;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.stark.triggerxbackend.common.exception.OtpLockedException;

import java.security.SecureRandom;
import java.time.Duration;

@Component
public class OtpStore {

    private static final Duration OTP_TTL = Duration.ofMinutes(5);
    private static final Duration SEND_TTL = Duration.ofHours(1);
    private static final Duration BLOCK_TTL = Duration.ofMinutes(15);

    private static final int MAX_ATTEMPTS = 5;
    private static final int MAX_SENDS = 3;

    private static final SecureRandom RANDOM = new SecureRandom();

    private final StringRedisTemplate redis;

    @Value("${security.otp.secret}")
    private String serverSecret;

    public OtpStore(StringRedisTemplate redis) {
        this.redis = redis;
    }

    // ================= OTP GENERATION =================

    public String generateAndStore(String email) {
        email = normalize(email);

        if (isBlocked(email)) {
            throw new OtpLockedException(retryAfterSeconds(email));
        }

        enforceSendLimit(email);

        String otp = generateOtp();
        String hash = hashOtp(otp);

        redis.opsForValue().set(otpKey(email), hash, OTP_TTL);
        redis.delete(attemptKey(email));

        return otp; // 🔥 RETURN OTP
    }

    // ================= OTP VERIFY =================

    public boolean verify(String email, String otp) {
        email = normalize(email);

        String storedHash = redis.opsForValue().get(otpKey(email));
        if (storedHash == null) return false;

        boolean match = storedHash.equals(hashOtp(otp));
        if (!match) registerFailure(email);

        return match;
    }

    public void markSuccess(String email) {
        email = normalize(email);
        redis.delete(otpKey(email));
        redis.delete(attemptKey(email));
        redis.delete(sendKey(email));
    }

    // ================= ABUSE PROTECTION =================

    public boolean isBlocked(String email) {
        return Boolean.TRUE.equals(redis.hasKey(blockKey(normalize(email))));
    }

    public long retryAfterSeconds(String email) {
        Long ttl = redis.getExpire(blockKey(normalize(email)));
        return ttl == null || ttl < 0 ? 0 : ttl;
    }

    private void registerFailure(String email) {
        Long attempts = redis.opsForValue().increment(attemptKey(email));

        if (attempts != null && attempts == 1) {
            redis.expire(attemptKey(email), OTP_TTL);
        }

        if (attempts != null && attempts >= MAX_ATTEMPTS) {
            redis.opsForValue().set(blockKey(email), "1", BLOCK_TTL);
        }
    }

    private void enforceSendLimit(String email) {
        Long sends = redis.opsForValue().increment(sendKey(email));

        if (sends != null && sends == 1) {
            redis.expire(sendKey(email), SEND_TTL);
        }

        if (sends != null && sends > MAX_SENDS) {
            throw new OtpLockedException(SEND_TTL.getSeconds());
        }
    }

    // ================= UTIL =================

    private String generateOtp() {
        return String.valueOf(100_000 + RANDOM.nextInt(900_000));
    }

    private String hashOtp(String otp) {
        return DigestUtils.sha256Hex(otp + serverSecret);
    }

    private String normalize(String email) {
        return email.trim().toLowerCase();
    }

    private String otpKey(String email) { return "otp:auth:" + email; }
    private String attemptKey(String email) { return "otp:attempt:" + email; }
    private String sendKey(String email) { return "otp:send:" + email; }
    private String blockKey(String email) { return "otp:block:" + email; }
}
