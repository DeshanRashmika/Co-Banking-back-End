package edu.icet.banking.accounts.api;

import edu.icet.banking.accounts.api.dto.AccountResponse;
import edu.icet.banking.accounts.api.dto.BalanceResponse;
import edu.icet.banking.accounts.application.AccountService;
import edu.icet.banking.auth.infrastructure.security.BankingUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping
    public List<AccountResponse> getAccounts(Authentication authentication) {
        return accountService.getAccounts(((BankingUserDetails) authentication.getPrincipal()).getUsername());
    }

    @GetMapping("/{id}")
    public AccountResponse getAccount(@PathVariable Long id, Authentication authentication) {
        return accountService.getAccount(id, ((BankingUserDetails) authentication.getPrincipal()).getUsername());
    }

    @GetMapping("/{id}/balance")
    public BalanceResponse getBalance(@PathVariable Long id, Authentication authentication) {
        return accountService.getBalance(id, ((BankingUserDetails) authentication.getPrincipal()).getUsername());
    }
}

