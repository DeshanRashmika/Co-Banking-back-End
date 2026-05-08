package edu.icet.banking.auth.infrastructure.repository;

import edu.icet.banking.auth.domain.entity.RefreshToken;
import edu.icet.banking.auth.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByUser(User user);
}

