package org.stark.triggerxbackend.auth.controller;

import org.stark.triggerxbackend.auth.dto.LoginRequest;
import org.stark.triggerxbackend.auth.dto.LoginResponse;
import org.stark.triggerxbackend.auth.dto.RegisterRequest;
import org.stark.triggerxbackend.auth.dto.RegisterResponse;
import org.stark.triggerxbackend.auth.service.AuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public RegisterResponse register(@RequestBody RegisterRequest request) {
        return authService.register(request);
    }


    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }


}
