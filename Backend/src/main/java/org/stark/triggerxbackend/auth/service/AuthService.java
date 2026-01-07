package org.stark.triggerxbackend.auth.service;

import org.springframework.stereotype.Service;
import org.stark.triggerxbackend.auth.dto.*;
import org.stark.triggerxbackend.auth.util.JwtUtil;
import org.stark.triggerxbackend.user.model.User;
import org.stark.triggerxbackend.user.repository.UserRepository;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    public RegisterResponse register(RegisterRequest request) {

        userRepository.findByEmail(request.email())
                .ifPresent(u -> {
                    throw new IllegalStateException("Email already registered");
                });

        String hash = encoder.encode(request.password());
        User user = new User(request.email(), hash);

        userRepository.save(user);

        return new RegisterResponse(user.getId(), user.getEmail());
    }

    public LoginTokenResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.email())
                .orElse(null);

        if (user == null) {
            throw new IllegalStateException("Invalid email or password");
        }

        boolean matches = encoder.matches(
                request.password(),
                user.getPasswordHash()
        );

        if (!matches) {
            throw new IllegalStateException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(user.getEmail());

        return new LoginTokenResponse(token, user.getEmail());
    }

}
