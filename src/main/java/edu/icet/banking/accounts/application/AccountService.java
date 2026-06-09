package edu.icet.banking.accounts.application;

import edu.icet.banking.accounts.api.dto.AccountRequest;
import edu.icet.banking.accounts.api.dto.AccountResponse;
import edu.icet.banking.accounts.api.dto.BalanceResponse;
import edu.icet.banking.accounts.domain.entity.Account;
import edu.icet.banking.accounts.domain.entity.AccountStatus;
import edu.icet.banking.accounts.infrastructure.repository.AccountRepository;
import edu.icet.banking.auth.domain.entity.User;
import edu.icet.banking.auth.infrastructure.repository.UserRepository;
import edu.icet.banking.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public List<AccountResponse> getAccounts(String email) {
        return accountRepository.findAllByUser_Email(email).stream().map(AccountResponse::from).toList();
    }

    public AccountResponse getAccount(Long id, String email) {
        Account account = accountRepository.findByIdAndUser_Email(id, email)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", id));
        return AccountResponse.from(account);
    }

    public BalanceResponse getBalance(Long id, String email) {
        Account account = accountRepository.findByIdAndUser_Email(id, email)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", id));
        return BalanceResponse.builder().accountId(account.getId()).balance(account.getBalance()).build();
    }

    @Transactional
    public AccountResponse createAccount(AccountRequest request, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        Account account = Account.builder()
                .user(user)
                .accountNumber(generateAccountNumber())
                .accountType(request.getAccountType())
                .balance(BigDecimal.ZERO)
                .currency(request.getCurrency())
                .status(AccountStatus.ACTIVE)
                .build();

        account = accountRepository.save(account);
        return AccountResponse.from(account);
    }

    private String generateAccountNumber() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}
