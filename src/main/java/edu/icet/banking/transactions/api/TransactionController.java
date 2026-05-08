package edu.icet.banking.transactions.api;

import edu.icet.banking.auth.domain.entity.User;
import edu.icet.banking.transactions.api.dto.TransactionResponse;
import edu.icet.banking.transactions.api.dto.TransferRequest;
import edu.icet.banking.transactions.application.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping("/{accountId}")
    public List<TransactionResponse> getTransactions(@PathVariable Long accountId, Authentication authentication) {
        return transactionService.getTransactions(accountId, ((User) authentication.getPrincipal()).getEmail());
    }

    @PostMapping("/transfer")
    public TransactionResponse transfer(@Valid @RequestBody TransferRequest request, Authentication authentication) {
        return transactionService.transfer(request, ((User) authentication.getPrincipal()).getEmail());
    }
}

