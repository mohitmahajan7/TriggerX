package org.stark.triggerxbackend.auth.service;

import org.springframework.stereotype.Service;
import org.stark.triggerxbackend.auth.dto.LoginRequest;
import org.stark.triggerxbackend.auth.dto.LoginResponse;
import org.stark.triggerxbackend.auth.dto.RegisterRequest;
import org.stark.triggerxbackend.auth.dto.RegisterResponse;
import org.stark.triggerxbackend.user.model.User;
import org.stark.triggerxbackend.user.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
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


    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.email())
                .orElse(null);

        if (user == null) {
            return new LoginResponse(false, "Invalid email or password");
        }

        boolean matches = encoder.matches(
                request.password(),
                user.getPasswordHash()
        );

        if (!matches) {
            return new LoginResponse(false, "Invalid email or password");
        }

        return new LoginResponse(true, "Login successful");
    }

}
