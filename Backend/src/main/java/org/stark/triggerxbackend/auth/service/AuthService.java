package org.stark.triggerxbackend.auth.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.stark.triggerxbackend.auth.dto.*;
import org.stark.triggerxbackend.auth.event.OtpEventPayload;
import org.stark.triggerxbackend.auth.event.OtpEventProducer;
import org.stark.triggerxbackend.auth.otp.OtpStore;
import org.stark.triggerxbackend.auth.util.JwtUtil;
import org.stark.triggerxbackend.user.model.User;
import org.stark.triggerxbackend.user.repository.UserRepository;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final OtpStore otpStore;
    private final OtpEventProducer otpEventProducer;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AuthService(
            UserRepository userRepository,
            JwtUtil jwtUtil,
            OtpStore otpStore,
            OtpEventProducer otpEventProducer
    ) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.otpStore = otpStore;
        this.otpEventProducer = otpEventProducer;
    }

    // ================= REGISTER (OTP ONLY) =================

    public RegisterResponse register(RegisterRequest request) {

        userRepository.findByEmail(request.email())
                .ifPresent(u -> {
                    throw new IllegalStateException("Email already registered");
                });

        // 1️⃣ Generate + store OTP (Redis TTL)
        String otp = otpStore.generateAndStore(request.email());

        // Build OTP event
        OtpEventPayload payload = new OtpEventPayload(
                "EMAIL_OTP_REQUESTED",
                request.email(),
                otp,
                "REGISTER"
        );

        // Publish event
        try {
            String json = objectMapper.writeValueAsString(payload);
            otpEventProducer.send(json);
        } catch (Exception e) {
            throw new RuntimeException("Failed to publish OTP event", e);
        }

        return new RegisterResponse("OTP sent");
    }

    // ================= VERIFY OTP =================

    public LoginTokenResponse verifyOtp(OtpVerifyRequest request) {

        String storedOtp = otpStore.read(request.email());

        if (storedOtp == null) {
            throw new IllegalStateException("OTP expired");
        }

        if (!storedOtp.equals(request.otp())) {
            throw new IllegalStateException("Invalid OTP");
        }

        // OTP valid → delete
        otpStore.delete(request.email());

        // Create user
        String hash = encoder.encode(request.password());
        User user = new User(request.email(), hash);
        userRepository.save(user);

        // Issue JWT
        String token = jwtUtil.generateToken(user.getEmail());

        return new LoginTokenResponse(token, user.getEmail());
    }

    // ================= LOGIN =================

    public LoginTokenResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalStateException("Invalid email or password"));

        if (!encoder.matches(request.password(), user.getPasswordHash())) {
            throw new IllegalStateException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(user.getEmail());
        return new LoginTokenResponse(token, user.getEmail());
    }
}
