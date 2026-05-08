package edu.icet.banking.investments.infrastructure.repository;

import edu.icet.banking.investments.domain.entity.Investment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvestmentRepository extends JpaRepository<Investment, Long> {
    List<Investment> findAllByUser_EmailOrderByPurchasedAtDesc(String email);
    Optional<Investment> findByIdAndUser_Email(Long id, String email);
}

