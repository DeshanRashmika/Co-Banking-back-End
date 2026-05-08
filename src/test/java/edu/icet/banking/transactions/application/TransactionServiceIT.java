package edu.icet.banking.transactions.application;

import edu.icet.banking.accounts.domain.entity.Account;
import edu.icet.banking.accounts.domain.entity.AccountStatus;
import edu.icet.banking.accounts.domain.entity.AccountType;
import edu.icet.banking.accounts.infrastructure.repository.AccountRepository;
import edu.icet.banking.auth.domain.entity.User;
import edu.icet.banking.auth.infrastructure.repository.UserRepository;
import edu.icet.banking.common.exception.InsufficientFundsException;
import edu.icet.banking.notifications.application.NotificationService;
import edu.icet.banking.transactions.api.dto.TransferRequest;
import edu.icet.banking.transactions.infrastructure.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.UnexpectedRollbackException;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class TransactionServiceIT {

    @Autowired private TransactionService transactionService;
    @Autowired private AccountRepository accountRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private TransactionRepository transactionRepository;
    @Autowired private NotificationService notificationService;

    @Test
    void transfer_shouldDebitAndCreditAccounts() {
        User user = userRepository.save(User.builder()
                .email("alice@example.com")
                .firstName("Alice")
                .lastName("Doe")
                .passwordHash("x")
                .build());

        Account from = accountRepository.save(Account.builder()
                .user(user)
                .accountNumber("A-100")
                .accountType(AccountType.CHECKING)
                .balance(new BigDecimal("1000.00"))
                .currency("USD")
                .status(AccountStatus.ACTIVE)
                .build());
        Account to = accountRepository.save(Account.builder()
                .user(user)
                .accountNumber("A-200")
                .accountType(AccountType.SAVINGS)
                .balance(new BigDecimal("250.00"))
                .currency("USD")
                .status(AccountStatus.ACTIVE)
                .build());

        transactionService.transfer(TransferRequest.builder()
                .fromAccountId(from.getId())
                .toAccountId(to.getId())
                .amount(new BigDecimal("100.00"))
                .description("Test transfer")
                .build(), user.getEmail());

        assertEquals(0, new BigDecimal("900.00").compareTo(accountRepository.findById(from.getId()).orElseThrow().getBalance()));
        assertEquals(0, new BigDecimal("350.00").compareTo(accountRepository.findById(to.getId()).orElseThrow().getBalance()));
        assertEquals(1, transactionRepository.count());
    }

    @Test
    void transfer_shouldRollbackWhenInsufficientFunds() {
        User user = userRepository.save(User.builder()
                .email("bob@example.com")
                .firstName("Bob")
                .lastName("Doe")
                .passwordHash("x")
                .build());

        Account from = accountRepository.save(Account.builder()
                .user(user)
                .accountNumber("B-100")
                .accountType(AccountType.CHECKING)
                .balance(new BigDecimal("10.00"))
                .currency("USD")
                .status(AccountStatus.ACTIVE)
                .build());
        Account to = accountRepository.save(Account.builder()
                .user(user)
                .accountNumber("B-200")
                .accountType(AccountType.SAVINGS)
                .balance(new BigDecimal("10.00"))
                .currency("USD")
                .status(AccountStatus.ACTIVE)
                .build());

        assertThrows(InsufficientFundsException.class, () -> transactionService.transfer(TransferRequest.builder()
                .fromAccountId(from.getId())
                .toAccountId(to.getId())
                .amount(new BigDecimal("100.00"))
                .build(), user.getEmail()));

        assertEquals(0, new BigDecimal("10.00").compareTo(accountRepository.findById(from.getId()).orElseThrow().getBalance()));
        assertEquals(0, new BigDecimal("10.00").compareTo(accountRepository.findById(to.getId()).orElseThrow().getBalance()));
        assertEquals(0, transactionRepository.count());
    }
}

