package edu.icet.banking.auth.api;

import edu.icet.banking.auth.api.dto.AuthResponse;
import edu.icet.banking.auth.api.dto.GoogleAuthRequest;
import edu.icet.banking.auth.api.dto.LoginRequest;
import edu.icet.banking.auth.api.dto.RegisterRequest;
import edu.icet.banking.auth.api.dto.UserResponse;
import edu.icet.banking.auth.application.AuthService;
import edu.icet.banking.auth.infrastructure.security.BankingUserDetails;
import edu.icet.banking.common.exception.ResourceNotFoundException;
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

    @GetMapping("/me")
    public UserResponse me(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new ResourceNotFoundException("User", "session", "not found");
        }
        String email = ((BankingUserDetails) authentication.getPrincipal()).getUsername();
        return authService.getCurrentUser(email);
    }

    @PostMapping("/logout")
    public void logout(Authentication authentication) {
        authService.logout(((BankingUserDetails) authentication.getPrincipal()).getUsername());
    }
}
