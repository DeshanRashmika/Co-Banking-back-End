package edu.icet.banking.transactions.infrastructure.repository;

import edu.icet.banking.transactions.domain.entity.BankTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<BankTransaction, Long> {
    List<BankTransaction> findAllByFromAccount_IdOrToAccount_IdOrderByCreatedAtDesc(Long fromAccountId, Long toAccountId);
}

