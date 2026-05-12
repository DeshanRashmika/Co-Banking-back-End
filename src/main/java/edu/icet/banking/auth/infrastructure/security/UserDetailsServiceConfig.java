package edu.icet.banking.auth.infrastructure.security;

import edu.icet.banking.auth.infrastructure.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Configuration
@AllArgsConstructor
@SuppressWarnings("unused")
public class UserDetailsServiceConfig {

    private final UserRepository userRepository;

    @Bean
    public UserDetailsService userDetailsService() {
        return email -> userRepository.findByEmail(email)
                .map(BankingUserDetails::from)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }
}
