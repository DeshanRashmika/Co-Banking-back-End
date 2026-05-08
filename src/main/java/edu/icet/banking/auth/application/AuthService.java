package edu.icet.banking.auth.application;

import edu.icet.banking.auth.api.dto.AuthResponse;
import edu.icet.banking.auth.api.dto.GoogleAuthRequest;
import edu.icet.banking.auth.api.dto.LoginRequest;
import edu.icet.banking.auth.api.dto.RegisterRequest;
import edu.icet.banking.auth.api.mapper.AuthMapper;
import edu.icet.banking.auth.domain.entity.RefreshToken;
import edu.icet.banking.auth.domain.entity.User;
import edu.icet.banking.auth.infrastructure.repository.RefreshTokenRepository;
import edu.icet.banking.auth.infrastructure.repository.UserRepository;
import edu.icet.banking.auth.infrastructure.security.GoogleTokenVerifier;
import edu.icet.banking.auth.infrastructure.security.JwtTokenProvider;
import edu.icet.banking.common.exception.InvalidOperationException;
import edu.icet.banking.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final GoogleTokenVerifier googleTokenVerifier;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new InvalidOperationException("Email is already registered");
        }

        User user = User.builder()
                .email(request.getEmail().toLowerCase())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .build();

        user = userRepository.save(user);
        return buildAuthResponse(user);
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        User user = userRepository.findByEmail(request.getEmail().toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", request.getEmail()));

        if (user.getPasswordHash() == null) {
            throw new InvalidOperationException("Use Google sign-in for this account");
        }

        return buildAuthResponse(user);
    }

    @Transactional
    public AuthResponse googleLogin(GoogleAuthRequest request) {
        var idToken = googleTokenVerifier.verify(request.getIdToken());
        if (idToken == null) {
            throw new InvalidOperationException("Invalid Google token");
        }

        String email = idToken.getPayload().getEmail().toLowerCase();
        String googleId = idToken.getPayload().getSubject();
        String firstName = (String) idToken.getPayload().get("given_name");
        String lastName = (String) idToken.getPayload().get("family_name");

        User user = userRepository.findByEmail(email).orElseGet(() -> userRepository.save(
                User.builder()
                        .email(email)
                        .googleId(googleId)
                        .firstName(firstName != null ? firstName : "Google")
                        .lastName(lastName != null ? lastName : "User")
                        .build()));

        if (user.getGoogleId() == null) {
            user.setGoogleId(googleId);
            user = userRepository.save(user);
        }

        return buildAuthResponse(user);
    }

    @Transactional
    public void logout(String email) {
        User user = userRepository.findByEmail(email.toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        refreshTokenRepository.deleteByUser(user);
    }

    private AuthResponse buildAuthResponse(User user) {
        String token = jwtTokenProvider.generateTokenFromEmail(user.getEmail());
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(token + ".refresh")
                .expiryDate(LocalDateTime.now().plusDays(30))
                .build();
        refreshTokenRepository.save(refreshToken);

        return AuthResponse.builder()
                .token(token)
                .user(AuthMapper.toUserResponse(user))
                .build();
    }
}
