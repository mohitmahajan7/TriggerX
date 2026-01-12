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

        return new RegisterResponse("OTP sent", request.email());
    }

    // ================= VERIFY OTP =================

    public LoginTokenResponse verifyOtp(OtpVerifyRequest request) {

        // 1️⃣ Check lock
        if (otpStore.isLocked(request.email())) {
            throw new IllegalStateException("Too many OTP attempts. Please resend OTP.");
        }

        // 2️⃣ Read OTP
        String storedOtp = otpStore.read(request.email());
        if (storedOtp == null) {
            throw new IllegalStateException("OTP expired");
        }

        // 3️⃣ Compare
        if (!storedOtp.equals(request.otp())) {
            otpStore.incrementAttempt(request.email());

            int left = otpStore.attemptsLeft(request.email());
            throw new IllegalStateException(
                    "Invalid OTP. Attempts left: " + left
            );
        }

        // 4️⃣ Success → cleanup
        otpStore.delete(request.email());

        // 5️⃣ Create user
        String hash = encoder.encode(request.password());
        User user = new User(request.email(), hash);
        userRepository.save(user);

        // 6️⃣ Issue JWT
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

    public LogoutResponse logout() {
        // Stateless JWT → nothing to invalidate server-side
        return new LogoutResponse("Logged out successfully");
    }


    public RegisterResponse resendOtp(ResendOtpRequest request) {

        String otp = otpStore.generateAndStore(request.email());

        OtpEventPayload payload = new OtpEventPayload(
                "EMAIL_OTP_RESENT",
                request.email(),
                otp,
                "REGISTER"
        );

        try {
            String json = objectMapper.writeValueAsString(payload);
            otpEventProducer.send(json);
        } catch (Exception e) {
            throw new RuntimeException("Failed to publish OTP resend event", e);
        }

        return new RegisterResponse("OTP resent", request.email());
    }


}
