package edu.icet.banking.accounts.api;

import edu.icet.banking.accounts.api.dto.AccountResponse;
import edu.icet.banking.accounts.api.dto.BalanceResponse;
import edu.icet.banking.accounts.application.AccountService;
import edu.icet.banking.auth.domain.entity.User;
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
        return accountService.getAccounts(((User) authentication.getPrincipal()).getEmail());
    }

    @GetMapping("/{id}")
    public AccountResponse getAccount(@PathVariable Long id, Authentication authentication) {
        return accountService.getAccount(id, ((User) authentication.getPrincipal()).getEmail());
    }

    @GetMapping("/{id}/balance")
    public BalanceResponse getBalance(@PathVariable Long id, Authentication authentication) {
        return accountService.getBalance(id, ((User) authentication.getPrincipal()).getEmail());
    }
}

