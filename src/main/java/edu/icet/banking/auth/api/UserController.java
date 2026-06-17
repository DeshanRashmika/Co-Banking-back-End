package edu.icet.banking.auth.api;

import edu.icet.banking.auth.api.dto.PasswordChangeRequest;
import edu.icet.banking.auth.api.dto.UserResponse;
import edu.icet.banking.auth.api.dto.UserUpdateRequest;
import edu.icet.banking.auth.application.AuthService;
import edu.icet.banking.auth.infrastructure.security.BankingUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final AuthService authService;

    @GetMapping("/profile")
    public UserResponse getProfile(Authentication authentication) {
        String email = ((BankingUserDetails) authentication.getPrincipal()).getUsername();
        return authService.getCurrentUser(email);
    }

    @PutMapping("/profile")
    public UserResponse updateProfile(@Valid @RequestBody UserUpdateRequest request, Authentication authentication) {
        String email = ((BankingUserDetails) authentication.getPrincipal()).getUsername();
        return authService.updateProfile(email, request);
    }

    @PostMapping("/change-password")
    public void changePassword(@Valid @RequestBody PasswordChangeRequest request, Authentication authentication) {
        String email = ((BankingUserDetails) authentication.getPrincipal()).getUsername();
        authService.changePassword(email, request);
    }
}