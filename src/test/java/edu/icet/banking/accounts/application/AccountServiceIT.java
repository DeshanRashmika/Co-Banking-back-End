package edu.icet.banking.accounts.application;

import edu.icet.banking.accounts.api.dto.AccountRequest;
import edu.icet.banking.accounts.api.dto.AccountResponse;
import edu.icet.banking.accounts.domain.entity.AccountType;
import edu.icet.banking.accounts.infrastructure.repository.AccountRepository;
import edu.icet.banking.auth.domain.entity.User;
import edu.icet.banking.auth.infrastructure.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AccountServiceIT {

    @Autowired private AccountService accountService;
    @Autowired private AccountRepository accountRepository;
    @Autowired private UserRepository userRepository;

    @Test
    void createAccount_shouldSaveAccount() {
        User user = userRepository.save(User.builder()
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .passwordHash("x")
                .build());

        AccountRequest request = AccountRequest.builder()
                .accountType(AccountType.SAVINGS)
                .currency("USD")
                .build();

        AccountResponse response = accountService.createAccount(request, user.getEmail());

        assertNotNull(response.getId());
        assertEquals("SAVINGS", response.getAccountType());
        assertEquals("USD", response.getCurrency());
        assertEquals(0, BigDecimal.ZERO.compareTo(response.getBalance()));
        assertNotNull(response.getAccountNumber());
        assertEquals(10, response.getAccountNumber().length());

        List<AccountResponse> accounts = accountService.getAccounts(user.getEmail());
        assertEquals(1, accounts.size());
    }
}
