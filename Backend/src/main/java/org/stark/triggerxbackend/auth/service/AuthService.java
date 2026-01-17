package org.stark.triggerxbackend.auth.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.stark.triggerxbackend.auth.dto.*;
import org.stark.triggerxbackend.auth.event.OtpEventPayload;
import org.stark.triggerxbackend.auth.event.OtpEventProducer;
import org.stark.triggerxbackend.auth.event.UserEventProducer;
import org.stark.triggerxbackend.auth.event.UserRegisteredEvent;
import org.stark.triggerxbackend.auth.otp.OtpStore;
import org.stark.triggerxbackend.auth.util.JwtUtil;
import org.stark.triggerxbackend.common.exception.OtpInvalidException;
import org.stark.triggerxbackend.common.exception.OtpLockedException;
import org.stark.triggerxbackend.user.model.User;
import org.stark.triggerxbackend.user.repository.UserRepository;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final OtpStore otpStore;
    private final OtpEventProducer otpEventProducer;
    private final UserEventProducer userEventProducer;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public AuthService(UserRepository userRepository, JwtUtil jwtUtil, OtpStore otpStore, OtpEventProducer otpEventProducer, UserEventProducer userEventProducer) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.otpStore = otpStore;
        this.otpEventProducer = otpEventProducer;
        this.userEventProducer = userEventProducer;
    }

    // ================= REGISTER =================

    public RegisterResponse register(RegisterRequest request) {

        userRepository.findByEmail(request.email()).ifPresent(u -> {
            throw new IllegalStateException("Email already registered");
        });

        String otp = otpStore.generateAndStore(request.email());

        otpEventProducer.send(new OtpEventPayload("EMAIL_OTP_REQUESTED", request.email(), otp, "REGISTER"));

        return new RegisterResponse("OTP sent", request.email());
    }

    // ================= VERIFY OTP =================

    public LoginTokenResponse verifyOtp(OtpVerifyRequest request) {

        if (otpStore.isBlocked(request.email())) {
            throw new OtpLockedException(otpStore.retryAfterSeconds(request.email()));
        }

        boolean verified = otpStore.verify(request.email(), request.otp());
        if (!verified) {
            throw new OtpInvalidException("Invalid OTP");
        }

        otpStore.markSuccess(request.email());

        User user = new User(request.email(), encoder.encode(request.password()));
        userRepository.save(user);

        userEventProducer.send(
                new UserRegisteredEvent(
                        "USER_REGISTERED",
                        user.getEmail()
                )
        );


        return new LoginTokenResponse(jwtUtil.generateToken(user.getEmail()), user.getEmail());
    }

    // ================= RESEND OTP =================

    public RegisterResponse resendOtp(ResendOtpRequest request) {

        String otp = otpStore.generateAndStore(request.email());

        otpEventProducer.send(new OtpEventPayload("EMAIL_OTP_RESENT", request.email(), otp, "REGISTER"));

        return new RegisterResponse("OTP resent", request.email());
    }

    // ================= LOGIN =================

    public LoginTokenResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.email()).orElseThrow(() -> new IllegalStateException("Invalid email or password"));

        if (!encoder.matches(request.password(), user.getPasswordHash())) {
            throw new IllegalStateException("Invalid email or password");
        }

        return new LoginTokenResponse(jwtUtil.generateToken(user.getEmail()), user.getEmail());
    }

    // ================= LOGOUT =================

    public LogoutResponse logout() {
        return new LogoutResponse("Logged out successfully");
    }
}
