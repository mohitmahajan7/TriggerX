package org.stark.triggerxbackend.auth.otp;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.Duration;

@Component
public class OtpStore {

    private static final Duration OTP_TTL = Duration.ofMinutes(5);
    private static final SecureRandom RANDOM = new SecureRandom();

    private final StringRedisTemplate redisTemplate;


    public OtpStore(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public String generateAndStore(String email) {
        email = email.trim().toLowerCase();
        String otp = genrateOtp();
        String key = key(email);
        redisTemplate.opsForValue().set(key, otp, OTP_TTL);
        System.out.println("OTP STORED → " + key + " = " + otp);
        return otp;
    }

    public String read(String email) {
        email = email.trim().toLowerCase();
        String key = key(email);
        String otp = redisTemplate.opsForValue().get(key);
        System.out.println("OTP READ → " + key + " = " + otp);
        return otp;
    }


    public void delete(String email) {
        redisTemplate.delete(key(email));
    }

    private String genrateOtp(){
        return String.valueOf(100_00 + RANDOM.nextInt(900_000));

    }

    private String normalize(String email) {
        return email.trim().toLowerCase();
    }

    private String key(String email) {
        return "auth:otp:register:" + normalize(email);
    }


}
