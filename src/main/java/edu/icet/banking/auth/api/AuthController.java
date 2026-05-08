package edu.icet.banking.auth.api;

import edu.icet.banking.auth.api.dto.AuthResponse;
import edu.icet.banking.auth.api.dto.GoogleAuthRequest;
import edu.icet.banking.auth.api.dto.LoginRequest;
import edu.icet.banking.auth.api.dto.RegisterRequest;
import edu.icet.banking.auth.application.AuthService;
import edu.icet.banking.auth.domain.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/google")
    public AuthResponse google(@Valid @RequestBody GoogleAuthRequest request) {
        return authService.googleLogin(request);
    }

    @PostMapping("/logout")
    public void logout(Authentication authentication) {
        authService.logout(((User) authentication.getPrincipal()).getEmail());
    }
}

